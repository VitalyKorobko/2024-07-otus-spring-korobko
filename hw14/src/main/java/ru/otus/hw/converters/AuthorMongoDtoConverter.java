package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorMongoDto;

@Component
public class AuthorMongoDtoConverter {
    public String authorDtoToString(AuthorMongoDto authorMongoDto) {
        return "Id: %s, FullName: %s".formatted(authorMongoDto.getId(), authorMongoDto.getFullName());
    }
}
