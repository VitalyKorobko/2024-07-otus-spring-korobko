//package ru.otus.hw.rest;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//
//@SpringBootTest(classes = {GenreController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import({GenreRepository.class, GenreMapper.class})
//@EnableAutoConfiguration
//public class GenreControllerTest {
//    @MockBean
//    private GenreRepository repository;
//
//    @Autowired
//    private GenreMapper mapper;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Test
//    @DisplayName("должен возвращать список всех жанров")
//    void shouldReturnCorrectGenresList() {
//        List<GenreDto> genreDtoList = List.of(
//                new GenreDto("1", "genre_1"),
//                new GenreDto("2", "genre_2")
//        );
//
//        var genreFlux = Flux.fromIterable(genreDtoList);
//        given(repository.findAll()).willReturn(genreFlux.map(mapper::toGenre));
//
//        var result = webTestClient
//                .get().uri("/api/v1/genres")
//                .accept(MediaType.TEXT_EVENT_STREAM)
//                .exchange()
//                .expectStatus().isOk()
//                .returnResult(GenreDto.class)
//                .getResponseBody();
//
//        var step = StepVerifier.create(result);
//        StepVerifier.Step<GenreDto> stepResult = null;
//        for (GenreDto dto: genreDtoList) {
//            stepResult = step.expectNext(dto);
//        }
//        stepResult.verifyComplete();
//
//    }
//
//
//}
