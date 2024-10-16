package ru.otus.hw.repositories;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
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

@DisplayName("Репозиторий на основе MongoRepository для работы с книгами ")
@DataMongoTest
class BookRepositoryTest {
    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;

    private static final String FIRST_BOOK_ID = "1";

    private static final String FOURTH_BOOK_ID = "4";

    private static final String FIRST_AUTHOR_ID = "1";

    private static final String SECOND_AUTHOR_ID = "2";

    private static final String FIRST_GENRE_ID = "1";

    private static final String SECOND_GENRE_ID = "2";

    private static final String FIFTH_GENRE_ID = "5";

    private static final String SIXTH_GENRE_ID = "6";

    private static final String BOOK_TITLE = "BookTitle_10500";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoOperations operations;

    @DisplayName("должен загружать информацию о нужной книге по её id")
    @Test
    void shouldFindExpectedBookById() {
        var optionalActualBook = bookRepository.findById(FIRST_BOOK_ID);
        var expectedBook = operations.findById(FIRST_BOOK_ID, Book.class);
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBookListWithAllInfo() {
        var students = bookRepository.findAll();
        assertThat(students).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> !b.getTitle().equals(""))
                .allMatch(b -> !CollectionUtils.isEmpty(b.getGenres()))
                .allMatch(b -> Objects.nonNull(b.getAuthor().getFullName()));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var expectedBook = new Book(FOURTH_BOOK_ID, BOOK_TITLE,
                operations.findById(FIRST_AUTHOR_ID, Author.class),
                List.of(Objects.requireNonNull(operations.findById(FIRST_GENRE_ID, Genre.class)),
                        Objects.requireNonNull(operations.findById(SECOND_GENRE_ID, Genre.class))
                )
        );
        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> !book.getId().equals("0"))
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> !CollectionUtils.isEmpty(book.getGenres()))
                .matches(book -> Objects.nonNull(book.getAuthor()))
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(operations.findById(returnedBook.getId(), Book.class))
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(FIRST_BOOK_ID, BOOK_TITLE,
                operations.findById(SECOND_AUTHOR_ID, Author.class),
                List.of(Objects.requireNonNull(operations.findById(FIFTH_GENRE_ID, Genre.class)),
                        Objects.requireNonNull(operations.findById(SIXTH_GENRE_ID, Genre.class))
                )
        );

        assertThat(operations.findById(expectedBook.getId(), Book.class))
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> !book.getId().equals("0"))
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> !CollectionUtils.isEmpty(book.getGenres()))
                .matches(book -> Objects.nonNull(book.getAuthor()))
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(operations.findById(returnedBook.getId(), Book.class))
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBook() {
        var book = operations.findById(FIRST_BOOK_ID, Book.class);
        assertThat(book).isNotNull();
        bookRepository.deleteById(FIRST_BOOK_ID);
        assertThat(operations.findById(FIRST_BOOK_ID, Book.class)).isNull();
    }

    @DisplayName("не должен выбрасывать исключение при попытке удаления книги с несуществующим Id")
    @Test
    void shouldNotThrowExceptionWhenTryingToDeleteBookWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> bookRepository.deleteById(FOURTH_BOOK_ID));
    }

    @DisplayName("должен возвращать пустой Optional при попытке загрузки книги с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadBookWithNonExistentId() {
        assertThat(bookRepository.findById(FOURTH_BOOK_ID)).isEmpty();
    }


}
