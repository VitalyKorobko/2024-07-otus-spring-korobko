package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.HashMap;

@Component
public class BookMapper {
    public Book toBook(BookDto bookDto) {
        return new Book(
                bookDto.getId(),
                bookDto.getTitle(),
                new Author(bookDto.getAuthorId(), bookDto.getAuthorName()),
                bookDto.getMapGenres().entrySet().stream().map(e -> new Genre(e.getKey(), e.getValue())).toList()
        );
    }

    public BookDto toBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getAuthor().getFullName(),
                book.getGenres().stream().collect(HashMap::new,
                        (map, g) -> map.put(g.getId(), g.getName()), HashMap::putAll)
                );
    }


}
