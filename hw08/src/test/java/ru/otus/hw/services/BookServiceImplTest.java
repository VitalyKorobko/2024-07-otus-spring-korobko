package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.CommentMapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами ")
@DataMongoTest
@Import({BookServiceImpl.class, BookMapper.class, CommentServiceImpl.class, IdSequencesServiceImpl.class, CommentMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    private static final String FIRST_BOOK_ID = "1";

    private static final String THIRD_BOOK_ID = "3";

    private static final String FOURTH_BOOK_ID = "4";

    private static final String FIFTH_BOOK_ID = "5";

    private static final String FIRST_AUTHOR_ID = "1";

    private static final String FIRST_GENRE_ID = "1";

    private static final String SECOND_GENRE_ID = "2";

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
                .allMatch(bookDto -> !bookDto.getId().equals("0") && !bookDto.getTitle().isEmpty())
                .allMatch(bookDto -> !bookDto.getAuthorDto().getId().equals("0")
                        && !bookDto.getAuthorDto().getFullName().isEmpty())
                .allMatch(bookDto -> !bookDto.getListDtoGenres().isEmpty());
    }

    @DisplayName(" должен возвращать BookDto по id")
    @Test
    void shouldReturnBookDtoById() {
        var expectedGenreDto = new GenreDto(FIRST_GENRE_ID, FIRST_GENRE_NAME);
        var optionalBookDto = bookService.findById(FIRST_BOOK_ID);
        assertThat(optionalBookDto).isPresent()
                .get()
                .matches(bookDto -> Objects.equals(bookDto.getId(), FIRST_BOOK_ID)
                        && BOOK_TITLE.equals(bookDto.getTitle()))
                .matches(bookDto -> Objects.equals(bookDto.getAuthorDto().getId(), FIRST_AUTHOR_ID)
                        && AUTHOR_NAME.equals(bookDto.getAuthorDto().getFullName()))
                .matches(bookDto -> bookDto.getListDtoGenres().contains(expectedGenreDto));
    }

    @DisplayName(" должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var returnedBookDto = bookService.insert(
                BOOK_TITLE,
                FIRST_AUTHOR_ID,
                Set.of(SECOND_GENRE_ID)
        );

        assertThat(returnedBookDto).isNotNull()
                .matches(bookDto -> Objects.equals(bookDto.getId(), FOURTH_BOOK_ID )
                        && bookDto.getTitle().equals(BOOK_TITLE))
                .matches(bookDto -> Objects.equals(bookDto.getAuthorDto().getId(), FIRST_AUTHOR_ID)
                        && bookDto.getAuthorDto().getFullName().equals(AUTHOR_NAME))
                .matches(bookDto -> Objects.equals(bookDto.getListDtoGenres().get(0).getId(), SECOND_GENRE_ID)
                        && bookDto.getListDtoGenres().get(0).getName().equals(SECOND_GENRE_NAME));

    }

    @DisplayName(" должен сохранять измененную книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveChangedBook() {
        var optionalBook = bookService.findById(THIRD_BOOK_ID);
        assertThat(optionalBook).isPresent().get()
                .matches(bookDto -> Objects.equals(bookDto.getId(), THIRD_BOOK_ID)
                        && !bookDto.getTitle().equals(BOOK_TITLE));

        var returnedBook = bookService.update(
                THIRD_BOOK_ID,
                BOOK_TITLE,
                FIRST_AUTHOR_ID,
                Set.of(FIRST_GENRE_ID)
        );

        assertThat(returnedBook).isNotNull()
                .matches(booDto -> booDto.getId().equals(THIRD_BOOK_ID) && booDto.getTitle().equals(BOOK_TITLE))
                .matches(booDto -> booDto.getAuthorDto().getId().equals(FIRST_AUTHOR_ID)
                        && booDto.getAuthorDto().getFullName().equals(AUTHOR_NAME))
                .matches(booDto -> booDto.getListDtoGenres().get(0).getId().equals(FIRST_GENRE_ID)
                        && booDto.getListDtoGenres().get(0).getName().equals(FIRST_GENRE_NAME));

    }

    @DisplayName(" должен удалять книгу по id")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBookById() {
        var optionalBookDto = bookService.findById(THIRD_BOOK_ID);
        assertThat(optionalBookDto).isPresent();
        bookService.deleteById(THIRD_BOOK_ID);
        assertThat(bookService.findById(THIRD_BOOK_ID)).isNotPresent();
    }

    @DisplayName(" должен выбрасывать исключение при попытке удаления книги с несуществующим Id")
    @Test
    void shouldNotThrowExceptionWhenTryingToDeleteBookWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> bookService.deleteById(FIFTH_BOOK_ID));
    }

    @DisplayName(" должен возвращать пустой Optional при попытке загрузки книги с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadBookWithNonExistentId() {
        assertThat(bookService.findById(FIFTH_BOOK_ID)).isEmpty();
    }


}
