package ru.otus.hw.mapper;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.GenreMongoDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreMapper {

    public GenreMongoDto toDto(Genre genre) {
        return new GenreMongoDto(
                new ObjectId().toString(),
                genre.getName()
        );
    }

    public GenreDto toGenreDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }

}
