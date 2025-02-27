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
import ru.otus.hw.dto.QuantityDto;
import ru.otus.hw.dto.QuantityDtoWeb;
import ru.otus.hw.mapper.QuantityMapper;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.repository.QuantityRepository;
import ru.otus.hw.security.SecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = {QuantityController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Import({QuantityMapper.class, SecurityConfiguration.class})
public class QuantityControllerTest {

    private static final String PRODUCT_COUNT_MESSAGE = "Количество продукта целое число";

    private static final String PRODUCT_ID_MESSAGE = "id продукта не должно быть пустым";

    @MockBean
    private QuantityRepository quantityRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private QuantityMapper quantityMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректный список информацмм по количеству продукта")
    void shouldReturnCorrectQuantitiesList() {
        List<Quantity> quantities = List.of(
                new Quantity("1", 1000, "1"),
                new Quantity("2", 1000, "2")
        );

        var quantityFlux = Flux.fromIterable(quantities);
        given(quantityRepository.findAll()).willReturn(quantityFlux);
        var result = webTestClient
                .get().uri("/api/v1/quantity")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(QuantityDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<QuantityDto> stepResult = null;
        for (QuantityDto dto : quantities.stream().map(p -> quantityMapper.toQuantityDto(p)).toList()) {
            stepResult = step.expectNext(dto);
        }
        stepResult.verifyComplete();
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректное значение по id")
    void shouldReturnCorrectQuantityById() {
        var quantity = new Quantity("1", 1000, "1");

        var quantityMono = Mono.just(quantity);
        System.out.println(port);
        given(quantityRepository.findByProductId("1")).willReturn(quantityMono);
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/v1/quantity/1")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(QuantityDto.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result).isEqualTo(quantityMapper.toQuantityDto(quantity));
    }

    @Test
    @WithMockUser
    @DisplayName("должен корректно сохранять новое значение")
    void shouldCorrectSaveNewQuantity() {
        var quantity = new Quantity("1", 1000, "1");

        var quantityMono = Mono.just(quantity);
        given(quantityRepository.save(any())).willReturn(quantityMono);

        var result = webTestClient
                .post().uri("/api/v1/quantity")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(quantityMapper.toQuantityDto(quantity)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(QuantityDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(quantityMapper.toQuantityDtoWeb(quantity, null));
    }

    @Test
    @WithMockUser
    @DisplayName("должен корректно обновлять значение")
    void shouldCorrectUpdateQuantity() throws Exception {
        var quantity = new Quantity("1", 1000, "1");
        var savedQuantity = new Quantity("1", 2000, "1");
        var quantityMono = Mono.just(quantity);
        given(quantityRepository.findById("1")).willReturn(quantityMono);
        given(quantityRepository.save(any())).willReturn(Mono.just(savedQuantity));

        var result = webTestClient
                .patch().uri("/api/v1/quantity/1")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(quantityMapper.toQuantityDto(quantity)))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(QuantityDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(quantityMapper.toQuantityDtoWeb(savedQuantity, null));
    }

    @Test
    @WithMockUser
    @DisplayName("Должен корректно удалять значение")
    void shouldCorrectDeleteQuantity() throws Exception {
        webTestClient
                .delete().uri("/api/v1/quantity/1")
                .exchange()
                .expectStatus()
                .isOk();
        verify(quantityRepository, times(1)).deleteById("1");
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле productCount")
    void shouldReturnMessageIfProductCountValueIsIncorrect() {
        var quantity = new Quantity("1", 1000, "1");

        var quantityDto = quantityMapper.toQuantityDto(quantity);
        quantityDto.setProductCount(-1);

        var quantityMono = Mono.just(quantity);
        given(quantityRepository.save(any())).willReturn(quantityMono);

        var result = webTestClient
                .post().uri("/api/v1/quantity")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(quantityDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(QuantityDtoWeb.class)
                .getResponseBody()
                .blockLast();
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(PRODUCT_COUNT_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле productId")
    void shouldReturnMessageIfProductIdValueIsIncorrect() {
        var quantity = new Quantity("1", 1000, "1");
        var quantityDto = quantityMapper.toQuantityDto(quantity);
        quantityDto.setProductId("");

        var quantityMono = Mono.just(quantity);
        given(quantityRepository.save(any())).willReturn(quantityMono);

        var result = webTestClient
                .post().uri("/api/v1/quantity")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(quantityDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(QuantityDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(PRODUCT_ID_MESSAGE);
    }

}

