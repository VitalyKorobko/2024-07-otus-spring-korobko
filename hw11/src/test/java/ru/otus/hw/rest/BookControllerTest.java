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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = {BookController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({BookRepository.class, GenreRepository.class, AuthorRepository.class,
        BookMapper.class, AuthorMapper.class, GenreMapper.class})
@EnableAutoConfiguration
public class BookControllerTest {
    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private GenreMapper genreMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("должен возвращать корректный список всех книг")
    void shouldReturnCorrectBooksList() {
        List<BookDto> bookDtoList = List.of(
                new BookDto(
                        "1",
                        "title_111",
                        new AuthorDto("1", "author_111"),
                        List.of(new GenreDto("1", "genre_111")))
        );

        var authorFlux = Flux.fromIterable(bookDtoList);
        given(bookRepository.findAll()).willReturn(authorFlux.map(bookMapper::toBook));

        var result = webTestClient
                .get().uri("/api/v1/book")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = null;
        for (BookDto dto : bookDtoList) {
            stepResult = step.expectNext(dto);
        }
        stepResult.verifyComplete();

    }

    @Test
    @DisplayName("должен возвращать корректную книгу по id")
    void shouldReturnCorrectBookById() {
        var bookDto = new BookDto(
                "1",
                "title_111",
                new AuthorDto("1", "author_111"),
                List.of(new GenreDto("1", "genre_111"))
        );

        var bookMono = Mono.just(bookDto);
        given(bookRepository.findById("1")).willReturn(bookMono.map(bookMapper::toBook));

        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/v1/book/1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookDto.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("должен корректно сохранять новую книгу")
    void shouldCorrectSaveNewBook() {
        var authorDto = new AuthorDto("1", "author_111");
        var setGenreDto = List.of(new GenreDto("1", "genre_111"));
        var bookDto = new BookDto(
                "1",
                "title_111",
                authorDto,
                setGenreDto
        );

        var bookMono = Mono.just(bookMapper.toBook(bookDto));
        given(bookRepository.findById("1")).willReturn(bookMono);
        given(authorRepository.findById("1")).willReturn(Mono.just(authorMapper.toAuthor(authorDto)));
        given(genreRepository.findAllByIdIn(Set.of("1"))).willReturn(Flux.fromIterable(
                setGenreDto.stream().map(genreDto -> genreMapper.toGenre(genreDto)).collect(Collectors.toSet())
        ));
        given(bookRepository.save(any())).willReturn(Mono.just(bookMapper.toBook(bookDto)));

        var result = webTestClient
                .post().uri("/api/v1/book")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookMapper.toBookDtoWeb(bookDto, null)))
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(BookDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(bookMapper.toBookDtoWeb(bookDto, null));
    }


    @Test
    @DisplayName("должен корректно обновлять книгу")
    void shouldCorrectUpdateBook() throws Exception {
        var authorDto = new AuthorDto("1", "author_111");
        var setGenreDto = List.of(new GenreDto("1", "genre_111"));
        var bookDto = new BookDto(
                "1",
                "title_111",
                authorDto,
                setGenreDto
        );
        var expectedBookDto = new BookDto(
                "1",
                "title_222",
                new AuthorDto("2", "author_222"),
                List.of(new GenreDto("2", "genre_222"))
        );
        var bookMono = Mono.just(bookMapper.toBook(bookDto));
        given(bookRepository.findById("1")).willReturn(bookMono);
        given(authorRepository.findById("1")).willReturn(Mono.just(authorMapper.toAuthor(authorDto)));
        given(genreRepository.findAllByIdIn(Set.of("1"))).willReturn(Flux.fromIterable(
                setGenreDto.stream().map(genreDto -> genreMapper.toGenre(genreDto)).collect(Collectors.toSet())
        ));
        given(bookRepository.save(any())).willReturn(Mono.just(bookMapper.toBook(expectedBookDto)));

        var result = webTestClient
                .patch().uri("/api/v1/book/1")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookMapper.toBookDtoWeb(bookDto, null)))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BookDtoWeb.class)
                .getResponseBody();

        assertThat(result.blockLast()).isEqualTo(bookMapper.toBookDtoWeb(expectedBookDto, null));
    }


    @Test
    @DisplayName("Должен корректно удалять книгу")
    void shouldCorrectDeleteBook() throws Exception {
        webTestClient
                .delete().uri("/api/v1/book/1")
                .exchange()
                .expectStatus()
                .isOk();
        verify(bookRepository, times(1)).deleteById("1");
    }

}
