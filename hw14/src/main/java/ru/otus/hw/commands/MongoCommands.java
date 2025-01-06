package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorMongoDtoConverter;
import ru.otus.hw.converters.BookMongoDtoConverter;
import ru.otus.hw.converters.CommentMongoDtoConverter;
import ru.otus.hw.converters.GenreMongoDtoConverter;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoGenreRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoCommentRepository;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class MongoCommands {

    private final MongoAuthorRepository mongoAuthorRepository;

    private final MongoGenreRepository mongoGenreRepository;

    private final MongoBookRepository mongoBookRepository;

    private final MongoCommentRepository mongoCommentRepository;

    private final AuthorMongoDtoConverter authorMongoDtoConverter;

    private final GenreMongoDtoConverter genreMongoDtoConverter;

    private final BookMongoDtoConverter bookMongoDtoConverter;

    private final CommentMongoDtoConverter commentMongoDtoConverter;

    @ShellMethod(value = "getAllAuthorsFromMongoDb", key = "amo")
    public String getAllAuthors() throws Exception {
        return mongoAuthorRepository.findAll().stream()
                .map(authorMongoDtoConverter::authorDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllGenresFromMongoDb", key = "gmo")
    public String getAllGenres() throws Exception {
        return mongoGenreRepository.findAll().stream()
                .map(genreMongoDtoConverter::genreDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllBooksFromMongoDb", key = "bmo")
    public String getAllBooks() throws Exception {
        return mongoBookRepository.findAll().stream()
                .map(bookMongoDtoConverter::bookDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "getAllCommentsFromMongoDb", key = "cmo")
    public String getAllComments() throws Exception {
        return mongoCommentRepository.findAll().stream()
                .map(commentMongoDtoConverter::commentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

}
