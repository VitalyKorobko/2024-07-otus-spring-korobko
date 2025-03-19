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
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.dto.ProductDtoWeb;
import ru.otus.hw.mapper.ProductMapper;
import ru.otus.hw.model.Product;
import ru.otus.hw.security.SecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.services.ProductService;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = {ProductController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Import({ProductMapper.class, SecurityConfiguration.class})
public class ProductControllerTest {

    private static final String TITLE_MESSAGE = "название продукта должно быть более 3 сиволов";

    private static final String REF_MESSAGE = "введите артикул продукта, минимум 4 сивола";

    private static final String IMAGE_MESSAGE = "в поле image должна быть ссылка на картинку";

    private static final String DESCRIPTION_MESSAGE = "Поле описание товара не более 255 сиволов";

    private static final String PRICE_MESSAGE = "Цена - любое целое число";

    private static final String SELLER_ID_MESSAGE = "id - продавца целое число";

    @MockBean
    private ProductService productService;

    @LocalServerPort
    private int port;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректный список всех товаров")
    void shouldReturnCorrectProductsList() {
        List<Product> products = List.of(
                new Product("1", "title1", "ref1", "https://1", "description1", 100, 1),
                new Product("2", "title2", "ref2", "https://2", "description2", 200, 1)
        );

        var productFlux = Flux.fromIterable(products);
        given(productService.findAll()).willReturn(productFlux.map(o -> productMapper.toProductDto(o)));
        var result = webTestClient
                .get().uri("/api/v1/product")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<ProductDto> stepResult = null;
        for (ProductDto dto : products.stream().map(p -> productMapper.toProductDto(p)).toList()) {
            System.out.println(dto);
            stepResult = step.expectNext(dto);
        }
        stepResult.verifyComplete();

    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать корректный товар по id")
    void shouldReturnCorrectProductById() {
        var product = new Product("1", "title1", "ref1",
                "https://1", "description1", 100, 1);

        var productMono = Mono.just(product);
        System.out.println(port);
        given(productService.findById("1")).willReturn(productMono.map(o -> productMapper.toProductDto(o)));
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/v1/product/1")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .timeout(Duration.ofSeconds(3))
                .block();
        System.out.println(result);
        assertThat(result).isEqualTo(productMapper.toProductDto(product));
    }

    @Test
    @WithMockUser
    @DisplayName("должен корректно сохранять новый товар")
    void shouldCorrectSaveNewProduct() {
        var product = new Product("100", "title1", "ref12",
                "https://1", "description1", 100, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(productMapper.toProductDtoWeb(product, null));
    }


    @Test
    @WithMockUser
    @DisplayName("должен корректно обновлять товар")
    void shouldCorrectUpdateProduct() throws Exception {
        var product = new Product("1", "title1", "ref12",
                "https://1", "description1", 100, 1);
        var savedProduct = new Product("1", "TITLE1", "ref12",
                "https://1", "description1", 100, 1);
        var productMono = Mono.just(product);
        given(productService.findById("1")).willReturn(productMono.map(o -> productMapper.toProductDto(product)));
        given(productService.save(any()))
                .willReturn(Mono.just(productMapper.toProductDtoWeb(savedProduct, null)));

        var result = webTestClient
                .patch().uri("/api/v1/product/1")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(productMapper.toProductDtoWeb(savedProduct, null));
    }


    @Test
    @WithMockUser
    @DisplayName("Должен корректно удалять товар")
    void shouldCorrectDeleteProduct() throws Exception {
        webTestClient
                .delete().uri("/api/v1/product/1")
                .exchange()
                .expectStatus()
                .isOk();
        verify(productService, times(1)).deleteById("1");
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле title")
    void shouldReturnMessageIfTitleValueIsIncorrect() {
        var product = new Product("1", "Q", "ref12",
                "https://1", "description1", 100, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(TITLE_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле ref")
    void shouldReturnMessageIfRefValueIsIncorrect() {
        var product = new Product("100", "title1", "QQQ",
                "https://1", "description1", 100, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(REF_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле image")
    void shouldReturnMessageIfImageValueIsIncorrect() {
        var product = new Product("100", "title1", "ref12",
                "htt://", "description1", 100, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(IMAGE_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле description")
    void shouldReturnMessageIfDescriptionValueIsIncorrect() {
        var product = new Product("100", "title1", "ref12",
                "https://1",
                IntStream.range(0, 256).mapToObj((i) -> "a").collect(Collectors.joining()),
                100, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(DESCRIPTION_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле price")
    void shouldReturnMessageIfPriceValueIsIncorrect() {
        var product = new Product("100", "title1", "ref12",
                "https://1", "description1", -1, 1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(PRICE_MESSAGE);
    }

    @Test
    @WithMockUser
    @DisplayName("должен возвращать сообщение в поле message при некорретных данных в поле sellerId")
    void shouldReturnMessageIfSellerIdValueIsIncorrect() {
        var product = new Product("100", "title1", "ref12",
                "https://1", "description1", 100, -1);

        var productMono = Mono.just(product);
        given(productService.save(any()))
                .willReturn(productMono.map(o -> productMapper.toProductDtoWeb(o, null)));

        var result = webTestClient
                .post().uri("/api/v1/product")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(productMapper.toProductDto(product)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ProductDtoWeb.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo(SELLER_ID_MESSAGE);
    }


}
