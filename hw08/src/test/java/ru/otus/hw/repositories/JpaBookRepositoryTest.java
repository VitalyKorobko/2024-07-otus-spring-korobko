package ru.otus.hw.repositories;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataMongoTest
class JpaBookRepositoryTest {
    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;

    private static final long FIRST_BOOK_ID = 1L;

    private static final long FOURTH_BOOK_ID = 4L;

    private static final long FIRST_AUTHOR_ID = 1L;

    private static final long SECOND_AUTHOR_ID = 2L;

    private static final long FIRST_GENRE_ID = 1L;

    private static final long SECOND_GENRE_ID = 2L;

    private static final long FIFTH_GENRE_ID = 5L;

    private static final long SIXTH_GENRE_ID = 6L;

    private static final String BOOK_TITLE = "BookTitle_10500";


    @Autowired
    private BookRepository repositoryJpa;

    @DisplayName(" должен загружать информацию о нужной книге по её id")
    @Test
    void shouldFindExpectedBookById() {
        var optionalActualBook = repositoryJpa.findById(FIRST_BOOK_ID);
        Book expectedBook = new Book(
                FIRST_BOOK_ID,
                "BookTitle_" + FIRST_BOOK_ID,
                new Author(FIRST_AUTHOR_ID, "Author_" + FIRST_AUTHOR_ID),
                List.of(new Genre(FIRST_GENRE_ID, "Genre_" + FIRST_GENRE_ID),
                        new Genre(SECOND_GENRE_ID, "Genre_" + SECOND_GENRE_ID)
                )
        );
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBookListWithAllInfo() {
        var students = repositoryJpa.findAll();
        assertThat(students).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> !b.getTitle().equals(""))
                .allMatch(b -> !CollectionUtils.isEmpty(b.getGenres()))
                .allMatch(b -> Objects.nonNull(b.getAuthor().getFullName()));
        System.out.println("\n====================================================================\n");
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var expectedBook = new Book(FOURTH_BOOK_ID, BOOK_TITLE, new Author(FIRST_AUTHOR_ID, "Author_" + FIRST_AUTHOR_ID),
                List.of(new Genre(FIRST_GENRE_ID, "Genre_" + FIRST_GENRE_ID),
                        new Genre(SECOND_GENRE_ID, "Genre_" + SECOND_GENRE_ID)
                )
        );
        var returnedBook = repositoryJpa.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> !CollectionUtils.isEmpty(book.getGenres()))
                .matches(book -> Objects.nonNull(book.getAuthor()))
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(repositoryJpa.findById(returnedBook.getId())).isPresent().get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, BOOK_TITLE, new Author(FIRST_AUTHOR_ID, "Author_" + SECOND_AUTHOR_ID),
                List.of(new Genre(FIRST_GENRE_ID, "Genre_" + FIFTH_GENRE_ID),
                        new Genre(SECOND_GENRE_ID, "Genre_" + SIXTH_GENRE_ID)
                )
        );

        assertThat(repositoryJpa.findById(expectedBook.getId())).isPresent().get()
                .isNotEqualTo(expectedBook);

        var returnedBook = repositoryJpa.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> !CollectionUtils.isEmpty(book.getGenres()))
                .matches(book -> Objects.nonNull(book.getAuthor()))
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(repositoryJpa.findById(returnedBook.getId())).isPresent().get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBook() {
        var bookOptional = repositoryJpa.findById(FIRST_BOOK_ID);
        assertThat(bookOptional).isPresent().get().isNotNull();
        repositoryJpa.deleteById(FIRST_BOOK_ID);
        assertThat(repositoryJpa.findById(FIRST_BOOK_ID)).isEmpty();
    }

    @DisplayName("не должен выбрасывать исключение при попытке удаления книги с несуществующим Id")
    @Test
    void shouldNotThrowExceptionWhenTryingToDeleteBookWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> repositoryJpa.deleteById(4L));
    }

    @DisplayName("должен возвращать пустой Optional при попытке загрузки книги с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadBookWithNonExistentId() {
        assertThat(repositoryJpa.findById(4L)).isEmpty();
    }


}
