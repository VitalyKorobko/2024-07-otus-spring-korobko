package ru.otus.hw.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoRepository для работы с комментариями для книг")
@DataMongoTest
public class CommentRepositoryTest {
    private static final String FIRST_COMMENT_ID = "1";

    private static final String FOURTH_COMMENT_ID = "4";

    private static final String FIRST_BOOK_ID = "1";

    private static final String BOOK_ID = "2";

    private static final String TEXT_COMMENT = "comment";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoOperations operations;

    @DisplayName(" должен загружать список всех комментариев для книги по её id")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var actualComments = commentRepository.findByBookId(FIRST_BOOK_ID);
        var expectedComments = getDbComments(FIRST_BOOK_ID);
        assertThat(actualComments).
                isEqualTo(expectedComments);
    }

    @DisplayName(" должен загружать комментарий по его id")
    @Test
    void shouldReturnCorrectCommentById() {
        var actualOptionalComment = commentRepository.findById(FIRST_COMMENT_ID);
        var expectedComment = operations.findById(FIRST_COMMENT_ID, Comment.class);
        assertThat(actualOptionalComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName(" должен сохранять новый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewComment() {
        var expectedComment = new Comment(String.valueOf(FOURTH_COMMENT_ID), TEXT_COMMENT,
                operations.findById(BOOK_ID, Book.class));
        var returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> !c.getId().equals("0"))
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> Objects.equals(c.getBook().getId(), BOOK_ID))
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(operations.findById(returnedComment.getId(), Comment.class))
                .isEqualTo(returnedComment);
    }

    @DisplayName(" должен сохранять изменённый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedComment() {
        var expectedBook = operations.findById(BOOK_ID, Book.class);
        var expectedComment = new Comment(FIRST_COMMENT_ID, TEXT_COMMENT, expectedBook);

        assertThat(operations.findById(expectedComment.getId(), Comment.class))
                .isNotEqualTo(expectedComment);

        var returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(c -> !c.getId().equals("0"))
                .matches(c -> c.getText().equals(TEXT_COMMENT))
                .matches(c -> Objects.equals(c.getBook().getId(), BOOK_ID))
                .usingRecursiveComparison().isEqualTo(expectedComment);

        assertThat(operations.findById(returnedComment.getId(), Comment.class))
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteComment() {
        var comment = operations.findById(FIRST_COMMENT_ID, Comment.class);
        assertThat(comment).isNotNull();
        commentRepository.deleteById(FIRST_COMMENT_ID);
        assertThat(operations.findById(FIRST_COMMENT_ID, Comment.class)).isNull();
    }

    @DisplayName("должен удалять все комментарии книги по id книги")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteAllCommentsByBookId() {
        var comments = operations.findAll(Comment.class);
        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(3);
        commentRepository.deleteAllByBookId(FIRST_BOOK_ID);
        assertThat(operations.findAll(Comment.class)).isEmpty();
    }

    @DisplayName("не должен выбрасывать исключение при попытке удаления комментария с несуществующим Id")
    @Test
    void shouldNotThrowExceptionWhenTryingToDeleteCommentWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> commentRepository.deleteById(FOURTH_COMMENT_ID));
    }

    @DisplayName("должен возвращать пустой Optional при попытке загрузки комментария с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadCommentWithNonExistentId() {
        assertThat(commentRepository.findById(FOURTH_COMMENT_ID)).isEmpty();
    }

    private static List<Comment> getDbComments(String bookId) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment(String.valueOf(id), "Comment_" + id,
                        new Book(
                                bookId,
                                "BookTitle_1",
                                new Author("1", "Author_1"),
                                List.of(new Genre("1", "Genre_1"), new Genre("2", "Genre_2"))
                        )))
                .toList();
    }

}

