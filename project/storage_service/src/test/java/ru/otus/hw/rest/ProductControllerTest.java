//package ru.otus.hw.rest;
//
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import ru.otus.hw.mapper.ProductMapper;
//import ru.otus.hw.repositories.AuthorRepository;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.times;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//
//@SpringBootTest(classes = {ProductController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import({ProductRepository.class, GenreRepository.class, AuthorRepository.class,
//        ProductMapper.class, AuthorMapper.class, GenreMapper.class})
//@EnableAutoConfiguration
//public class ProductControllerTest {
//    @MockBean
//    private ProductRepository productRepository;
//
//    @MockBean
//    private AuthorRepository authorRepository;
//
//    @MockBean
//    private GenreRepository genreRepository;
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private ProductMapper productMapper;
//
//    @Autowired
//    private AuthorMapper authorMapper;
//
//    @Autowired
//    private GenreMapper genreMapper;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Test
//    @DisplayName("должен возвращать корректный список всех товаров")
//    void shouldReturnCorrectProductsList() {
//        List<ProductDto> productDtoList = List.of(
//                new ProductDto(
//                        "1",
//                        "title_111",
//                        new AuthorDto("1", "author_111"),
//                        List.of(new GenreDto("1", "genre_111")))
//        );
//
//        var authorFlux = Flux.fromIterable(productDtoList);
//        given(productRepository.findAll()).willReturn(authorFlux.map(productMapper::toProduct));
//
//        var result = webTestClient
//                .get().uri("/api/v1/product")
//                .accept(MediaType.TEXT_EVENT_STREAM)
//                .exchange()
//                .expectStatus().isOk()
//                .returnResult(ProductDto.class)
//                .getResponseBody();
//
//        var step = StepVerifier.create(result);
//        StepVerifier.Step<ProductDto> stepResult = null;
//        for (ProductDto dto : productDtoList) {
//            stepResult = step.expectNext(dto);
//        }
//        stepResult.verifyComplete();
//
//    }
//
//    @Test
//    @DisplayName("должен возвращать корректный товар по id")
//    void shouldReturnCorrectProductById() {
//        var productDto = new ProductDto(
//                "1",
//                "title_111",
//                new AuthorDto("1", "author_111"),
//                List.of(new GenreDto("1", "genre_111"))
//        );
//
//        var productMono = Mono.just(productDto);
//        given(productRepository.findById("1")).willReturn(productMono.map(productMapper::toProduct));
//
//        var client = WebClient.create(String.format("http://localhost:%d", port));
//
//        var result = client
//                .get().uri("/api/v1/product/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(ProductDto.class)
//                .timeout(Duration.ofSeconds(3))
//                .block();
//
//        assertThat(result).isEqualTo(productDto);
//    }
//
//    @Test
//    @DisplayName("должен корректно сохранять новый товар")
//    void shouldCorrectSaveNewProduct() {
//        var authorDto = new AuthorDto("1", "author_111");
//        var setGenreDto = List.of(new GenreDto("1", "genre_111"));
//        var productDto = new ProductDto(
//                "1",
//                "title_111",
//                authorDto,
//                setGenreDto
//        );
//
//        var productMono = Mono.just(productMapper.toProduct(productDto));
//        given(productRepository.findById("1")).willReturn(productMono);
//        given(authorRepository.findById("1")).willReturn(Mono.just(authorMapper.toAuthor(authorDto)));
//        given(genreRepository.findAllByIdIn(Set.of("1"))).willReturn(Flux.fromIterable(
//                setGenreDto.stream().map(genreDto -> genreMapper.toGenre(genreDto)).collect(Collectors.toSet())
//        ));
//        given(productRepository.save(any())).willReturn(Mono.just(productMapper.toProduct(productDto)));
//
//        var result = webTestClient
//                .post().uri("/api/v1/product")
//                .contentType(APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productMapper.toProductDtoWeb(productDto, null)))
//                .exchange()
//                .expectStatus()
//                .isCreated()
//                .returnResult(ProductDtoWeb.class)
//                .getResponseBody();
//
//        assertThat(result.blockLast()).isEqualTo(productMapper.toProductDtoWeb(productDto, null));
//    }
//
//
//    @Test
//    @DisplayName("должен корректно обновлять товар")
//    void shouldCorrectUpdateProduct() throws Exception {
//        var authorDto = new AuthorDto("1", "author_111");
//        var setGenreDto = List.of(new GenreDto("1", "genre_111"));
//        var productDto = new ProductDto(
//                "1",
//                "title_111",
//                authorDto,
//                setGenreDto
//        );
//        var expectedProductDto = new ProductDto(
//                "1",
//                "title_222",
//                new AuthorDto("2", "author_222"),
//                List.of(new GenreDto("2", "genre_222"))
//        );
//        var productMono = Mono.just(productMapper.toProduct(productDto));
//        given(productRepository.findById("1")).willReturn(productMono);
//        given(authorRepository.findById("1")).willReturn(Mono.just(authorMapper.toAuthor(authorDto)));
//        given(genreRepository.findAllByIdIn(Set.of("1"))).willReturn(Flux.fromIterable(
//                setGenreDto.stream().map(genreDto -> genreMapper.toGenre(genreDto)).collect(Collectors.toSet())
//        ));
//        given(productRepository.save(any())).willReturn(Mono.just(productMapper.toProduct(expectedProductDto)));
//
//        var result = webTestClient
//                .patch().uri("/api/v1/product/1")
//                .contentType(APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productMapper.toProductDtoWeb(productDto, null)))
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .returnResult(ProductDtoWeb.class)
//                .getResponseBody();
//
//        assertThat(result.blockLast()).isEqualTo(productMapper.toProductDtoWeb(expectedProductDto, null));
//    }
//
//
//    @Test
//    @DisplayName("Должен корректно удалять товар")
//    void shouldCorrectDeleteProduct() throws Exception {
//        webTestClient
//                .delete().uri("/api/v1/product/1")
//                .exchange()
//                .expectStatus()
//                .isOk();
//        verify(productRepository, times(1)).deleteById("1");
//    }
//
//}
