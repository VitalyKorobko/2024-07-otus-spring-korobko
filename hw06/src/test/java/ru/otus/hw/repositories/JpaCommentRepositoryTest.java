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
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями для книг")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class})
public class JpaCommentRepositoryTest {

    private static final long FIRST_COMMENT_ID = 1L;

    private static final long BOOK_ID = 2L;

    private static final String TEXT_COMMENT = "comment";

    private static final int EXPECTED_QUERIES_COUNT = 1;

    @Autowired
    private JpaCommentRepository repositoryJpa;

    @Autowired
    TestEntityManager em;

    @DisplayName(" должен загружать список всех комментариев для книги по её id")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        System.out.println("\n===================================================================\n");
        var actualComments = repositoryJpa.findAllCommentsByBookId(1L);
        var expectedComments = getDbComments(1L);
        assertThat(actualComments).
                isEqualTo(expectedComments);
        System.out.println("\n====================================================================\n");
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
    }

    @DisplayName(" должен загружать комментарий по его id")
    @Test
    void shouldReturnCorrectCommentById() {
        var actualOptionalComment = repositoryJpa.findById(FIRST_COMMENT_ID);
        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(actualOptionalComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName(" должен сохранять новый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewComment() {
        var expectedComment = new Comment(0, TEXT_COMMENT, new Book(BOOK_ID));
        var returnedComment = repositoryJpa.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> c.getBookId() == BOOK_ID)
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(em.find(Comment.class, returnedComment.getId()))
                .isEqualTo(returnedComment);
    }

    @DisplayName(" должен сохранять изменённый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedComment() {
        var expectedBook = em.find(Book.class, BOOK_ID);
        var expectedComment = new Comment(FIRST_COMMENT_ID, TEXT_COMMENT, expectedBook);

        assertThat(em.find(Comment.class, expectedComment.getId()))
                .isNotEqualTo(expectedComment);

        var returnedComment = repositoryJpa.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> c.getBookId() == BOOK_ID)
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(em.find(Comment.class, returnedComment.getId()))
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteComment() {
        var comment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(comment).isNotNull();
        em.detach(comment);
        repositoryJpa.deleteById(FIRST_COMMENT_ID);
        assertThat(em.find(Comment.class, FIRST_COMMENT_ID)).isNull();
    }

    @DisplayName("должен выбрасывать исключение при попытке удаления комментария с несуществующим Id")
    @Test
    void shouldThrowExceptionWhenTryingToDeleteCommentWithNonExistentId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(4L));
    }

    @DisplayName("должен возвращать пустой Optional при попытке загрузки комментария с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadCommentWithNonExistentId() {
        assertThat(repositoryJpa.findById(4L)).isEmpty();
    }

    private static List<Comment> getDbComments(long bookId) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment((long) id, "Comment_" + id,
                        new Book(
                                bookId,
                                "BookTitle_1",
                                new Author(1, "Author_1"),
                                List.of(new Genre(1, "Genre_1"), new Genre(2, "Genre_2"))
                        )))
                .toList();
    }

}
