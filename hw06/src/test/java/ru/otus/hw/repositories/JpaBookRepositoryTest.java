package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;

    private static final long FIRST_BOOK_ID = 1L;

    private static final int EXPECTED_QUERIES_COUNT = 2;

    private static final String BOOK_TITLE = "BookTitle_10500";

    @Autowired
    private JpaBookRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @DisplayName(" должен загружать информацию о нужной книге по её id")
    @Test
    void shouldFindExpectedBookById() {
        var optionalActualBook = repositoryJpa.findById(FIRST_BOOK_ID);
        Book expectedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBookListWithAllInfo() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        System.out.println("\n===================================================================\n");
        var students = repositoryJpa.findAll();
        assertThat(students).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> !b.getTitle().equals(""))
                .allMatch(b -> b.getGenres() != null && b.getGenres().size() > 0)
                .allMatch(b -> b.getAuthor().getFullName() != null);
        System.out.println("\n====================================================================\n");
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, BOOK_TITLE, em.find(Author.class, 1),
                List.of(em.find(Genre.class, 1), em.find(Genre.class, 3)));
        var returnedBook = repositoryJpa.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> book.getGenres() != null && book.getGenres().size() > 0)
                .matches(book -> book.getAuthor() != null)
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(em.find(Book.class, returnedBook.getId()))
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, BOOK_TITLE, em.find(Author.class, 2),
                List.of(em.find(Genre.class, 5), em.find(Genre.class, 6)));

        assertThat(em.find(Book.class, expectedBook.getId()))
                .isNotEqualTo(expectedBook);

        var returnedBook = repositoryJpa.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getTitle().equals(BOOK_TITLE))
                .matches(book -> book.getGenres() != null && book.getGenres().size() > 0)
                .matches(book -> book.getAuthor() != null)
                .usingRecursiveComparison().isEqualTo(expectedBook);

        assertThat(em.find(Book.class, returnedBook.getId()))
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBook() {
        var book = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(book).isNotNull();
        em.detach(book);
        repositoryJpa.deleteById(FIRST_BOOK_ID);
        assertThat(em.find(Book.class, FIRST_BOOK_ID)).isNull();
    }

    @DisplayName("должен выбрасывать исключение при попытке удаления книги с несуществующим Id")
    @Test
    void shouldThrowExceptionWhenTryingToDeleteBookWithNonExistentId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(4L));
    }

    @DisplayName("должен возвращать пустой Optional при попытке загрузки книги с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadBookWithNonExistentId() {
        assertThat(repositoryJpa.findById(4L)).isEmpty();
    }


}
