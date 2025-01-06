package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreMongoDto;

@Component
public class GenreMongoDtoConverter {
    public String genreDtoToString(GenreMongoDto genreMongoDto) {
        return "Id: %s, Name: %s".formatted(genreMongoDto.getId(), genreMongoDto.getName());
    }

}
