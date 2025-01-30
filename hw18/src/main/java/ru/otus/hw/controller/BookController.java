package ru.otus.hw.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.BookService;

import java.util.List;

@Slf4j
@RestController
public class BookController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final BookService bookService;

    private final BookMapper mapper;


    public BookController(BookService bookService, BookMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @CircuitBreaker(name = "getAllBookCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToGetAllBook")
    @GetMapping("/api/v1/book")
    public List<BookDto> getBooks() {
        return bookService.findAll();
    }

    @CircuitBreaker(name = "getAnyBookCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToGetBook")
    @GetMapping("/api/v1/book/{id}")
    public BookDto getBook(@PathVariable("id") long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id = %d не найдена".formatted(id)));
    }

    @CircuitBreaker(name = "createBookCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToCreateBook")
    @PostMapping("/api/v1/book")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtoWeb saveBook(@Valid @RequestBody BookDtoWeb bookDtoWeb, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new BookDtoWeb(
                    0,
                    bookDtoWeb.getTitle(),
                    bookDtoWeb.getAuthorFullName(),
                    bookDtoWeb.getSetGenreNames(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        return mapper.toBookDtoWeb(
                bookService.insert(
                        bookDtoWeb.getTitle(),
                        bookDtoWeb.getAuthorFullName(),
                        bookDtoWeb.getSetGenreNames()),
                bookDtoWeb.getMessage()
        );
    }

    @CircuitBreaker(name = "updateBookCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToUpdateBook")
    @PatchMapping("/api/v1/book/{id}")
    public BookDtoWeb updateBook(@Valid @RequestBody BookDtoWeb bookDtoWeb,
                                 BindingResult bindingResult, @PathVariable("id") long id) {
        if (bindingResult.hasErrors()) {
            return new BookDtoWeb(
                    0,
                    bookDtoWeb.getTitle(),
                    bookDtoWeb.getAuthorFullName(),
                    bookDtoWeb.getSetGenreNames(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return mapper.toBookDtoWeb(
                bookService.update(
                        id,
                        bookDtoWeb.getTitle(),
                        bookDtoWeb.getAuthorFullName(),
                        bookDtoWeb.getSetGenreNames()),
                bookDtoWeb.getMessage()
        );
    }

    @DeleteMapping("/api/v1/book/{id}")
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }


    private List<BookDto> circuitBreakerFallBackToGetAllBook(Throwable e) {
        log.error("circuit breaker got open state when receiving book list. Err: {}: {}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private BookDto circuitBreakerFallBackToGetBook(long id, Throwable e) {
        log.error("circuit breaker got open state when receiving book with id={}: Err: {}:{}", id, e, e.getMessage());
        if (e.getClass() == EntityNotFoundException.class) {
            throw new EntityNotFoundException(e.getMessage());
        } else {
            throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
        }
    }

    private BookDtoWeb circuitBreakerFallBackToCreateBook(BookDtoWeb bookDtoWeb,
                                                       BindingResult bindingResult, Throwable e) {
        log.error("circuit breaker got open state when creating new book: {}. Err: {}: {}",
                bookDtoWeb, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private BookDtoWeb circuitBreakerFallBackToUpdateBook(BookDtoWeb bookDtoWeb,
                                                          BindingResult bindingResult, long id, Throwable e) {
        log.error("circuit breaker got open state when updating book: {}. Err: {}: {}", bookDtoWeb, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

}
