package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами ")
@DataJpaTest
@Import({BookServiceImpl.class, JpaAuthorRepository.class,
        JpaGenreRepository.class, JpaBookRepository.class, BookMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    private static final long FIRST_BOOK_ID = 1L;

    private static final long THIRD_BOOK_ID = 3L;

    private static final long FOURTH_BOOK_ID = 4L;

    private static final long FIFTH_BOOK_ID = 5L;

    private static final long FIRST_AUTHOR_ID = 1L;

    private static final long FIRST_GENRE_ID = 1L;

    private static final long SECOND_GENRE_ID = 2L;

    private static final String BOOK_TITLE = "BookTitle_1";

    private static final String AUTHOR_NAME = "Author_1";

    private static final String FIRST_GENRE_NAME = "Genre_1";

    private static final String SECOND_GENRE_NAME = "Genre_2";

    @Autowired
    private BookServiceImpl bookService;

    @DisplayName(" должен возвращать список BookDto")
    @Test
    void shouldFindAllBooks() {
        List<BookDto> books = bookService.findAll();
        assertThat(books)
                .allMatch(Objects::nonNull)
                .allMatch(bookDto -> bookDto.getId() != 0 && !bookDto.getTitle().isEmpty())
                .allMatch(bookDto -> bookDto.getAuthorId() != 0 && !bookDto.getAuthorName().isEmpty())
                .allMatch(bookDto -> !bookDto.getMapGenres().isEmpty());
    }

    @DisplayName(" должен возвращать BookDto по id")
    @Test
    void shouldReturnBookDtoById() {
        var optionalBookDto = bookService.findById(FIRST_BOOK_ID);
        assertThat(optionalBookDto).isPresent()
                .get()
                .matches(bookDto -> bookDto.getId() == FIRST_BOOK_ID && BOOK_TITLE.equals(bookDto.getTitle()))
                .matches(bookDto -> bookDto.getAuthorId() == FIRST_AUTHOR_ID && AUTHOR_NAME.equals(bookDto.getAuthorName()))
                .matches(bookDto -> bookDto.getMapGenres().get(FIRST_GENRE_ID).equals(FIRST_GENRE_NAME)
                        && bookDto.getMapGenres().get(SECOND_GENRE_ID).equals(SECOND_GENRE_NAME)
                );
    }

    @DisplayName(" должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var returnedBook = bookService.insert(
                BOOK_TITLE,
                FIRST_AUTHOR_ID,
                Set.of(SECOND_GENRE_ID)
        );

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() == FOURTH_BOOK_ID && book.getTitle().equals(BOOK_TITLE))
                .matches(book -> book.getAuthor().getId() == FIRST_AUTHOR_ID
                        && book.getAuthor().getFullName().equals(AUTHOR_NAME))
                .matches(book -> book.getGenres().get(0).getId() == SECOND_GENRE_ID
                        && book.getGenres().get(0).getName().equals(SECOND_GENRE_NAME));

    }

    @DisplayName(" должен сохранять измененную книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveChangedBook() {
        var optionalBook = bookService.findById(THIRD_BOOK_ID);
        assertThat(optionalBook).isPresent().get()
                .matches(bookDto -> bookDto.getId() == THIRD_BOOK_ID && !bookDto.getTitle().equals(BOOK_TITLE));

        var returnedBook = bookService.update(
                THIRD_BOOK_ID,
                BOOK_TITLE,
                FIRST_AUTHOR_ID,
                Set.of(FIRST_GENRE_ID)
        );

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() == THIRD_BOOK_ID && book.getTitle().equals(BOOK_TITLE))
                .matches(book -> book.getAuthor().getId() == FIRST_AUTHOR_ID
                        && book.getAuthor().getFullName().equals(AUTHOR_NAME))
                .matches(book -> book.getGenres().get(0).getId() == FIRST_GENRE_ID
                        && book.getGenres().get(0).getName().equals(FIRST_GENRE_NAME));

    }

    @DisplayName(" должен удалять книгу по id")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBookById() {
        var optionalBook = bookService.findById(THIRD_BOOK_ID);
        assertThat(optionalBook).isPresent();
        bookService.deleteById(THIRD_BOOK_ID);
        assertThat(bookService.findById(THIRD_BOOK_ID)).isNotPresent();
    }

    @DisplayName(" должен выбрасывать исключение при попытке удаления книги с несуществующим Id")
    @Test
    void shouldThrowExceptionWhenTryingToDeleteBookWithNonExistentId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.deleteById(FIFTH_BOOK_ID));
    }

    @DisplayName(" должен возвращать пустой Optional при попытке загрузки книги с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadBookWithNonExistentId() {
        assertThat(bookService.findById(FIFTH_BOOK_ID)).isEmpty();
    }


}
