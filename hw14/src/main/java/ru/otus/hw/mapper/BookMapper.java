package ru.otus.hw.mapper;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookMongoDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class BookMapper {

    private final MongoAuthorRepository mongoAuthorRepository;

    private final MongoGenreRepository mongoGenreRepository;


    public BookMapper(MongoAuthorRepository mongoAuthorRepository, MongoGenreRepository mongoGenreRepository) {
        this.mongoAuthorRepository = mongoAuthorRepository;
        this.mongoGenreRepository = mongoGenreRepository;
    }

    public BookMongoDto toDto(Book book) {
        return new BookMongoDto(
                new ObjectId().toString(),
                book.getTitle(),
                mongoAuthorRepository.findByFullName(book.getAuthor().getFullName()).get(0),
                mongoGenreRepository.findAllByNameIn(getSetGenres(book))
        );
    }

    public BookDto toBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName()),
                book.getGenres().stream().map(g -> new GenreDto(g.getId(), g.getName())).toList()
        );
    }

    private Set<String> getSetGenres(Book book) {
        return book.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
    }


}
