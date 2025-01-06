package ru.otus.hw.mapper;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentMongoDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    private final MongoBookRepository mongoBookRepository;

    private final MongoGenreRepository mongoGenreRepository;

    private final MongoAuthorRepository mongoAuthorRepository;

    public CommentMapper(MongoBookRepository mongoBookRepository,
                         MongoGenreRepository mongoGenreRepository,
                         MongoAuthorRepository mongoAuthorRepository) {
        this.mongoBookRepository = mongoBookRepository;
        this.mongoGenreRepository = mongoGenreRepository;
        this.mongoAuthorRepository = mongoAuthorRepository;
    }

    public CommentMongoDto toDto(Comment comment) {
        return new CommentMongoDto(
                new ObjectId().toString(),
                comment.getText(),
                mongoBookRepository.findByTitleAndAuthorMongoDtoAndGenreMongoDtoListIn(
                        comment.getBook().getTitle(),
                        mongoAuthorRepository.findByFullName(comment.getBook().getAuthor().getFullName()).get(0),
                        new HashSet<>(mongoGenreRepository.findAllByNameIn(getSetGenres(comment.getBook())))
                ).get(0)
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId()
        );
    }

    private Set<String> getSetGenres(Book book) {
        return book.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
    }

}
