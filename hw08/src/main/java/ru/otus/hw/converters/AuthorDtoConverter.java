package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;

@Component
public class AuthorDtoConverter {
    public String authorDtoToString(AuthorDto authorDto) {
        return "Id: %s, FullName: %s".formatted(authorDto.getId(), authorDto.getFullName());
    }
}
