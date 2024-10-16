package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorMapper {

    public Author toAuthor(AuthorDto authorDto) {
        return new Author(
                authorDto.getId(),
                authorDto.getFullName()
        );
    }

    public AuthorDto toAuthorDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFullName()
        );
    }

}
