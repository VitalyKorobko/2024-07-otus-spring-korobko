package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;


@Component
public class BookMapper {
    public Book toBook(BookDto bookDto) {
        return new Book(
                bookDto.getId(),
                bookDto.getTitle(),
                new Author(bookDto.getAuthorDto().getId(), bookDto.getAuthorDto().getFullName()),
                bookDto.getListDtoGenres().stream().map(gDto -> new Genre(gDto.getId(), gDto.getName())).toList()
        );
    }

    public BookDto toBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName()),
                book.getGenres().stream().map(g -> new GenreDto(g.getId(), g.getName())).toList()
        );
    }


}
