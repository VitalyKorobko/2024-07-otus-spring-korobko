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
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями ")
@DataJpaTest
@Import({CommentServiceImpl.class, JpaCommentRepository.class, JpaBookRepository.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImplTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_BOOK_ID = 2L;

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 3;

    private static final String FIRST_COMMENT_TEXT = "Comment_1";

    private static final String SECOND_COMMENT_TEXT = "Comment_2";

    private static final String THIRD_COMMENT_TEXT = "Comment_3";

    private static final long FIRST_COMMENT_ID = 1L;

    private static final long SECOND_COMMENT_ID = 2L;

    private static final long THIRD_COMMENT_ID = 3L;

    private static final long FOURTH_COMMENT_ID = 4L;

    private static final long FIFTH_COMMENT_ID = 5L;

    @Autowired
    private CommentServiceImpl commentService;

    @DisplayName(" должен возвращать список комментариев по id книги")
    @Test
    void shouldFindAllCommentsById() {
        List<Comment> comments = commentService.findAllCommentsByBookId(FIRST_BOOK_ID);
        assertThat(comments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(Objects::nonNull)
                .allMatch(comment -> comment.getId() != 0 && !comment.getText().isEmpty())
                .matches(c -> c.get(0).getId() == FIRST_COMMENT_ID
                        && c.get(0).getText().equals(FIRST_COMMENT_TEXT)
                        && c.get(0).getBook().getId() == FIRST_BOOK_ID)
                .matches(c -> c.get(1).getId() == SECOND_COMMENT_ID
                        && c.get(1).getText().equals(SECOND_COMMENT_TEXT)
                        && c.get(1).getBook().getId() == FIRST_BOOK_ID)
                .matches(c -> c.get(2).getId() == THIRD_COMMENT_ID
                        && c.get(2).getText().equals(THIRD_COMMENT_TEXT)
                        && c.get(2).getBook().getId() == FIRST_BOOK_ID);
    }

    @DisplayName(" должен возвращать комментарий по id")
    @Test
    void shouldReturnCommentById() {
        var optionalComment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(optionalComment).isPresent()
                .get()
                .matches(c -> c.getId() == FIRST_COMMENT_ID && c.getText().equals(FIRST_COMMENT_TEXT));
    }

    @DisplayName(" должен сохранять новый комментарий к книге")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewComment() {
        var returnedComment = commentService.insert(
                THIRD_COMMENT_TEXT,
                SECOND_BOOK_ID
        );

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() == FOURTH_COMMENT_ID
                        && comment.getText().equals(THIRD_COMMENT_TEXT))
                .matches(comment -> comment.getBook().getId() == SECOND_BOOK_ID);

    }

    @DisplayName(" должен сохранять измененный комментарий к книге")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveChangedComment() {
        var optionalComment = commentService.findById(THIRD_COMMENT_ID);
        assertThat(optionalComment).isPresent()
                .get()
                .matches(comment -> comment.getId() == THIRD_COMMENT_ID
                        && comment.getText().equals(THIRD_COMMENT_TEXT)
                        && comment.getBook().getId() == FIRST_BOOK_ID
                );

        var returnedComment = commentService.update(
                THIRD_COMMENT_ID,
                FIRST_COMMENT_TEXT,
                SECOND_BOOK_ID
        );

        assertThat(returnedComment).isNotNull()
                .matches(c -> c.getId() == THIRD_COMMENT_ID && c.getText().equals(FIRST_COMMENT_TEXT))
                .matches(c -> c.getBook().getId() == SECOND_BOOK_ID);
    }

    @DisplayName(" должен удалять комментарий по id")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteCommentById() {
        var optionalComment = commentService.findById(THIRD_COMMENT_ID);
        assertThat(optionalComment).isPresent();
        commentService.deleteById(THIRD_COMMENT_ID);
        assertThat(commentService.findById(THIRD_COMMENT_ID)).isNotPresent();
    }

    @DisplayName(" должен выбрасывать исключение при попытке удаления комментария с несуществующим Id")
    @Test
    void shouldThrowExceptionWhenTryingToDeleteCommentWithNonExistentId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(5L));
    }

    @DisplayName(" должен возвращать пустой Optional при попытке загрузки комментария с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadCommentWithNonExistentId() {
        assertThat(commentService.findById(FIFTH_COMMENT_ID)).isEmpty();
    }

}
