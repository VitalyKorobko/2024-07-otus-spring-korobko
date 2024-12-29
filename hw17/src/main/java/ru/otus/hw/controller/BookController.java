package ru.otus.hw.controller;

import jakarta.validation.Valid;
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
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;

    private final BookMapper mapper;

    public BookController(BookService bookService, BookMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @GetMapping("/api/v1/book")
    public List<BookDto> getBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public BookDto getBook(@PathVariable("id") long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id = %d не найдена".formatted(id)));
    }

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
        return mapper.toBookDtoWeb(bookService.insert(
                        bookDtoWeb.getTitle(),
                        bookDtoWeb.getAuthorFullName(),
                        bookDtoWeb.getSetGenreNames()
                ), bookDtoWeb.getMessage()
        );
    }

    @PatchMapping("/api/v1/book/{id}")
    public BookDtoWeb updateBook(@Valid @RequestBody BookDtoWeb bookDtoWeb,
                                 BindingResult bindingResult, @PathVariable("id") long id) {
        if (bindingResult.hasErrors()) {
            System.out.println("Genres");
            System.out.println(bindingResult.getFieldError().getField().toString());
            System.out.println(bookDtoWeb.getSetGenreNames().toString());
            return new BookDtoWeb(
                    0,
                    bookDtoWeb.getTitle(),
                    bookDtoWeb.getAuthorFullName(),
                    bookDtoWeb.getSetGenreNames(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return mapper.toBookDtoWeb(bookService.update(
                id,
                bookDtoWeb.getTitle(),
                bookDtoWeb.getAuthorFullName(),
                bookDtoWeb.getSetGenreNames()
        ), bookDtoWeb.getMessage());
    }

    @DeleteMapping("/api/v1/book/{id}")
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }


}
