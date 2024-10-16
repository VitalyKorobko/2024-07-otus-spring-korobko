package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookDtoConverter;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BookCommands {
    private final BookService bookService;

    private final BookDtoConverter bookDtoConverter;

    private final BookMapper bookMapper;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookDtoConverter::bookDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(long id) {
        return bookService.findById(String.valueOf(id))
                .map(bookDtoConverter::bookDtoToString)
                .orElse("Book with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, long authorId, Set<String> genresIds) {
        var savedBook = bookService.insert(title, String.valueOf(authorId), genresIds);
        return bookDtoConverter.bookDtoToString(savedBook);
    }

    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(long id, String title, long authorId, Set<String> genresIds) {
        var savedBook = bookService.update(String.valueOf(id), title, String.valueOf(authorId), genresIds);
        return bookDtoConverter.bookDtoToString(savedBook);
    }

    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBook(long id) {
        bookService.deleteById(String.valueOf(id));


    }
}
