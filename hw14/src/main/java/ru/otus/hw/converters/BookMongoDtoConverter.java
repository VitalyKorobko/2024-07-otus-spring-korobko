package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookMongoDto;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookMongoDtoConverter {
    private final AuthorMongoDtoConverter authorDtoConverter;

    private final GenreMongoDtoConverter genreDtoConverter;

    public String bookDtoToString(BookMongoDto bookMongoDto) {
        var genresString = bookMongoDto.getGenreMongoDtoList().stream()
                .map(genreDtoConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                bookMongoDto.getId(),
                bookMongoDto.getTitle(),
                authorDtoConverter.authorDtoToString(bookMongoDto.getAuthorMongoDto()),
                genresString);
    }
}
