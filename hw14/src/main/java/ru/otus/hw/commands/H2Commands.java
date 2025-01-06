package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookDtoConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.BookService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class H2Commands {
    private final JpaAuthorRepository jpaAuthorRepository;

    private final JpaGenreRepository jpaGenreRepository;

    private final BookService bookService;

    private final JpaCommentRepository jpaCommentRepository;

    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final BookDtoConverter bookDtoConverter;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "getAllAuthorsFromH2Db", key = "ah2")
    public String getAllAuthors() throws Exception {
        return jpaAuthorRepository.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllGenresFromH2Db", key = "gh2")
    public String getAllGenres() throws Exception {
        return jpaGenreRepository.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllBooksFromH2Db", key = "bh2")
    public String getAllBooks() throws Exception {
        return bookService.findAll().stream()
                .map(bookDtoConverter::bookDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllCommentsFromH2Db", key = "ch2")
    public String getAllComments() throws Exception {
        return jpaCommentRepository.findAll().stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }


}
