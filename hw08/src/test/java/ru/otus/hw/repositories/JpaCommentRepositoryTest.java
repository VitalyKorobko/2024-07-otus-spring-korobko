package ru.otus.hw.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями для книг")
@DataMongoTest
public class JpaCommentRepositoryTest {
    private static final long FIRST_COMMENT_ID = 1L;

    private static final long BOOK_ID = 2L;

    private static final String TEXT_COMMENT = "comment";

    @Autowired
    private CommentRepository repositoryJpa;

    @DisplayName(" должен загружать список всех комментариев для книги по её id")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var actualComments = repositoryJpa.findByBookId(1L);
        var expectedComments = getDbComments(1L);
        assertThat(actualComments).
                isEqualTo(expectedComments);
    }

    @DisplayName(" должен загружать комментарий по его id")
    @Test
    void shouldReturnCorrectCommentById() {
        var actualOptionalComment = repositoryJpa.findById(FIRST_COMMENT_ID);
        var expectedComment = new Comment(
                FIRST_COMMENT_ID,
                "Comment_" + FIRST_COMMENT_ID,
                new Book(1L,
                        "BookTitle_" + 1L,
                        new Author(1L, "Author_" + 1L),
                        List.of(new Genre(1L, "Genre_" + 1L),
                                new Genre(2L, "Genre_" + 2L)
                        )
                )
        );
        assertThat(actualOptionalComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName(" должен сохранять новый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewComment() {
        var expectedComment = new Comment(4L, TEXT_COMMENT, new Book(BOOK_ID));
        var returnedComment = repositoryJpa.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> Objects.equals(c.getBook().getId(), BOOK_ID))
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(repositoryJpa.findById(returnedComment.getId())).isPresent().get()
                .isEqualTo(returnedComment);
    }

    @DisplayName(" должен сохранять изменённый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedComment() {
        var expectedBook = new Book(BOOK_ID,
                "BookTitle_" + BOOK_ID,
                new Author(2L, "Author_" + 2L),
                List.of(new Genre(3L, "Genre_" + 3L),
                        new Genre(4L, "Genre_" + 4L)
                )
        );
        var expectedComment = new Comment(FIRST_COMMENT_ID, TEXT_COMMENT, expectedBook);

        assertThat(repositoryJpa.findById(expectedComment.getId())).isPresent().get()
                .isNotEqualTo(expectedComment);

        var returnedComment = repositoryJpa.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> Objects.equals(c.getBook().getId(), BOOK_ID))
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(repositoryJpa.findById(returnedComment.getId())).isPresent().get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteComment() {
        var commentOptional = repositoryJpa.findById(FIRST_COMMENT_ID);
        assertThat(commentOptional).isPresent().get().isNotNull();
        repositoryJpa.deleteById(FIRST_COMMENT_ID);
        assertThat(repositoryJpa.findById(FIRST_COMMENT_ID)).isEmpty();
    }

    @DisplayName("не должен выбрасывать исключение при попытке удаления комментария с несуществующим Id")
    @Test
    void shouldNotThrowExceptionWhenTryingToDeleteCommentWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> repositoryJpa.deleteById(4L));
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
