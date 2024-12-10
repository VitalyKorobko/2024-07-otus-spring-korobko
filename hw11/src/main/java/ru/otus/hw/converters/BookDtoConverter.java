package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookDtoConverter {
    private final AuthorDtoConverter authorDtoConverter;

    private final GenreDtoConverter genreDtoConverter;

    public String bookDtoToString(BookDto bookDto) {
        var genresString = bookDto.getListDtoGenres().stream()
                .map(genreDtoConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDto.getId(),
                bookDto.getTitle(),
                authorDtoConverter.authorDtoToString(bookDto.getAuthorDto()),
                genresString);
    }
}
