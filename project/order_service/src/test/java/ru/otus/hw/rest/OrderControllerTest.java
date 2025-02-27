package ru.otus.hw.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.enums.Status;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.domain.Order;
import ru.otus.hw.repository.OrderRepository;
import ru.otus.hw.security.SecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = {OrderController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Import({OrderMapper.class, SecurityConfiguration.class})
public class OrderControllerTest {

    private static final String STATUS_MESSAGE = "Статус должен быть выбран из четырех вариантов CURRENT, ISSUED, PAID, COMPLETED";

    private static final String START_DATE_MESSAGE = "начальная дата в секундах от 1970-01-01T00:00:00, макс. значение 365241780471";

    private static final String END_DATE_MESSAGE = "дата изменения в секундах от 1970-01-01T00:00:00, макс. значение 365241780471";

    private static final String ORDER_FIELD_MESSAGE = "Поле orderField не должно быть пустым";

    private static final String USER_ID_MESSAGE = "Должно быть целое положительное число";

    @MockBean
    private OrderRepository orderRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректный список всех заказов")
    void shouldReturnCorrectOrdersList() {
        List<Order> orders = List.of(
                new Order("1", Status.ISSUED, LocalDateTime.now(),
                        LocalDateTime.now(), "description1", 1),
                new Order("2", Status.PAID, LocalDateTime.now(),
                        LocalDateTime.now(), "description1", 1)
        );

        var orderFlux = Flux.fromIterable(orders);
        given(orderRepository.findAll()).willReturn(orderFlux);
        var result = webTestClient
                .get().uri("/api/v1/order")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(OrderDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<OrderDto> stepResult = null;
        for (OrderDto dto : orders.stream().map(p -> orderMapper.toOrderDto(p)).toList()) {
            System.out.println(dto);
            stepResult = step.expectNext(dto);
        }
        stepResult.verifyComplete();

    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректный заказ по id")
    void shouldReturnCorrectOrderById() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);

        var orderMono = Mono.just(order);
        System.out.println(port);
        given(orderRepository.findById("1")).willReturn(orderMono);
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/v1/order/1")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .timeout(Duration.ofSeconds(3))
                .block();
        System.out.println(result);
        assertThat(result).isEqualTo(orderMapper.toOrderDto(order));
    }

    @Test
    @WithMockUser
    @DisplayName("должен корректно сохранять новый заказ")
    void shouldCorrectSaveNewOrder() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderMapper.toOrderDto(order)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(orderMapper.toOrderDtoWeb(order, null));
    }


    @Test
    @WithMockUser
    @DisplayName("должен корректно обновлять заказ")
    void shouldCorrectUpdateOrder() throws Exception {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var savedOrder = new Order("1", Status.PAID, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderMono = Mono.just(order);
        given(orderRepository.findById("1")).willReturn(orderMono);
        given(orderRepository.save(any())).willReturn(Mono.just(savedOrder));

        var result = webTestClient
                .patch().uri("/api/v1/order/1")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderMapper.toOrderDto(order)))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(orderMapper.toOrderDtoWeb(savedOrder, null));
    }

    @Test
    @WithMockUser
    @DisplayName("Должен корректно удалять заказ")
    void shouldCorrectDeleteOrder() throws Exception {
        webTestClient
                .delete().uri("/api/v1/order/1")
                .exchange()
                .expectStatus()
                .isOk();
        verify(orderRepository, times(1)).deleteById("1");
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле status")
    void shouldReturnMessageIfStatusValueIsIncorrect() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderDto = orderMapper.toOrderDto(order);
        orderDto.setStatus("any");

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody()
                .blockLast();
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(STATUS_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле startDate")
    void shouldReturnMessageIfStartDateValueIsIncorrect() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderDto = orderMapper.toOrderDto(order);
        orderDto.setStartDate(-1);

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(START_DATE_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле endDate")
    void shouldReturnMessageIfEndDateValueIsIncorrect() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderDto = orderMapper.toOrderDto(order);
        orderDto.setEndDate(-1);

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(END_DATE_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле orderField")
    void shouldReturnMessageIfOrderFieldValueIsIncorrect() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderDto = orderMapper.toOrderDto(order);
        orderDto.setOrderField("");

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(ORDER_FIELD_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле userId")
    void shouldReturnMessageIfUserIdValueIsIncorrect() {
        var order = new Order("1", Status.ISSUED, LocalDateTime.now(),
                LocalDateTime.now(), "description1", 1);
        var orderDto = orderMapper.toOrderDto(order);
        orderDto.setUserId(-1);

        var orderMono = Mono.just(order);
        given(orderRepository.save(any())).willReturn(orderMono);

        var result = webTestClient
                .post().uri("/api/v1/order")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(orderDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(OrderDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(USER_ID_MESSAGE);
    }


}

