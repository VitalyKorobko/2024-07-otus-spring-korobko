package ru.otus.hw.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/api/v1/book/all")
    public List<BookDto> getBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public BookDto getBook(@PathVariable("id") long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id = %d не найдена".formatted(id)));
    }

    @PostMapping("/api/v1/book/new")
    public BookDto saveBook(@RequestBody BookDtoWeb bookDtoWeb) {
        return bookService.insert(
                bookDtoWeb.getTitle(),
                bookDtoWeb.getAuthorId(),
                bookDtoWeb.getSetGenresId()
        );
    }

    @PatchMapping("/api/v1/book")
    public BookDto updateBook(@RequestBody BookDtoWeb bookDtoWeb) {
        System.out.println(bookDtoWeb);
        return bookService.update(
                bookDtoWeb.getId(),
                bookDtoWeb.getTitle(),
                bookDtoWeb.getAuthorId(),
                bookDtoWeb.getSetGenresId()
        );
    }

    @DeleteMapping("/api/v1/book/{id}")
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> bookNotFound(EntityNotFoundException exception) {
        return ResponseEntity.badRequest().body("Ошибка! " + exception.getMessage());
    }


}
