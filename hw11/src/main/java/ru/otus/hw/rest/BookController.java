package ru.otus.hw.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.Set;


@RestController
public class BookController {
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookMapper bookMapper;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository,
                          GenreRepository genreRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookMapper = bookMapper;
    }

    @GetMapping("/api/v1/book")
    public Flux<BookDto> getBooks() {
        return bookRepository.findAll().map(bookMapper::toBookDto);
    }

    @GetMapping("/api/v1/book/{id}")
    public Mono<BookDto> getBook(@PathVariable("id") String id) {
        return bookRepository.findById(id).map(bookMapper::toBookDto);
    }

    @PostMapping("/api/v1/book")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDtoWeb> createBook(@Valid @RequestBody Mono<BookDtoWeb> monoBookDtoWeb) {
        return getMono(monoBookDtoWeb, null);
    }


    @PatchMapping("/api/v1/book/{id}")
    public Mono<BookDtoWeb> updateBook(@Valid @RequestBody Mono<BookDtoWeb> monoBookDtoWeb,
                                       @PathVariable("id") String id) {
        return getMono(monoBookDtoWeb, id);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookRepository.deleteById(id);
    }

    private Mono<BookDtoWeb> getMono(Mono<BookDtoWeb> monoBookDtoWeb, String id) {
        return monoBookDtoWeb
                .zipWhen(bookDtoWeb -> authorRepository.findById(bookDtoWeb.getAuthorId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("author with id = %s not found"
                                .formatted(bookDtoWeb.getAuthorId())))))
                .zipWhen(tuple -> genreRepository.findAllByIdIn(tuple.getT1().getSetGenresId())
                        .collect(() -> new ArrayList<Genre>(), ArrayList::add)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("genres not found"))))
                .flatMap(tuple2 ->
                        bookRepository.save(
                                new Book(id,
                                        tuple2.getT1().getT1().getTitle(),
                                        tuple2.getT1().getT2(),
                                        tuple2.getT2())))
                .map(book -> bookMapper.toBookDtoWeb(book, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new BookDtoWeb(
                                        ex.getFieldValue("id").toString(),
                                        ex.getFieldValue("title").toString(),
                                        ex.getFieldValue("authorId").toString(),
                                        Set.of(ex.getFieldValue("setGenresId").toString().split(",")),
                                        ex.getFieldError().getDefaultMessage()))
                );
    }


}
