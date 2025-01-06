package ru.otus.hw.mapper;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.AuthorMongoDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorMapper {

    public AuthorMongoDto toDto(Author author) {
        return new AuthorMongoDto(
                new ObjectId().toString(),
                author.getFullName()
        );
    }

    public AuthorDto toAuthorDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFullName()
        );
    }

}
