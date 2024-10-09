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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями ")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentMapper.class})
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
        List<CommentDto> comments = commentService.findAllCommentsByBookId(FIRST_BOOK_ID);
        assertThat(comments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(Objects::nonNull)
                .allMatch(commentDto -> commentDto.getId() != 0 && !commentDto.getText().isEmpty())
                .matches(c -> c.get(0).getId() == FIRST_COMMENT_ID
                        && c.get(0).getText().equals(FIRST_COMMENT_TEXT)
                        && c.get(0).getBookId() == FIRST_BOOK_ID)
                .matches(c -> c.get(1).getId() == SECOND_COMMENT_ID
                        && c.get(1).getText().equals(SECOND_COMMENT_TEXT)
                        && c.get(1).getBookId() == FIRST_BOOK_ID)
                .matches(c -> c.get(2).getId() == THIRD_COMMENT_ID
                        && c.get(2).getText().equals(THIRD_COMMENT_TEXT)
                        && c.get(2).getBookId() == FIRST_BOOK_ID);
    }

    @DisplayName(" должен возвращать комментарий по id")
    @Test
    void shouldReturnCommentById() {
        var optionalCommentDto = commentService.findById(FIRST_COMMENT_ID);
        assertThat(optionalCommentDto).isPresent()
                .get()
                .matches(c -> c.getId() == FIRST_COMMENT_ID && c.getText().equals(FIRST_COMMENT_TEXT));
    }

    @DisplayName(" должен сохранять новый комментарий к книге")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewComment() {
        var returnedCommentDto = commentService.insert(
                THIRD_COMMENT_TEXT,
                SECOND_BOOK_ID
        );

        assertThat(returnedCommentDto).isNotNull()
                .matches(c -> c.getId() == FOURTH_COMMENT_ID
                        && c.getText().equals(THIRD_COMMENT_TEXT))
                .matches(comment -> comment.getBookId() == SECOND_BOOK_ID);

    }

    @DisplayName(" должен сохранять измененный комментарий к книге")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveChangedComment() {
        var optionalCommentDto = commentService.findById(THIRD_COMMENT_ID);
        assertThat(optionalCommentDto).isPresent()
                .get()
                .matches(c -> c.getId() == THIRD_COMMENT_ID
                        && c.getText().equals(THIRD_COMMENT_TEXT)
                        && c.getBookId() == FIRST_BOOK_ID
                );

        var returnedCommentDto = commentService.update(
                THIRD_COMMENT_ID,
                FIRST_COMMENT_TEXT,
                SECOND_BOOK_ID
        );

        assertThat(returnedCommentDto).isNotNull()
                .matches(c -> c.getId() == THIRD_COMMENT_ID && c.getText().equals(FIRST_COMMENT_TEXT))
                .matches(c -> c.getBookId() == SECOND_BOOK_ID);
    }

    @DisplayName(" должен удалять комментарий по id")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteCommentById() {
        var optionalCommentDto = commentService.findById(THIRD_COMMENT_ID);
        assertThat(optionalCommentDto).isPresent();
        commentService.deleteById(THIRD_COMMENT_ID);
        assertThat(commentService.findById(THIRD_COMMENT_ID)).isNotPresent();
    }

    @DisplayName(" должен выбрасывать исключение при попытке удаления комментария с несуществующим Id")
    @Test
    void shouldThrowExceptionWhenTryingToDeleteCommentWithNonExistentId() {
        Assertions.assertDoesNotThrow(() -> commentService.deleteById(5L));
    }

    @DisplayName(" должен возвращать пустой Optional при попытке загрузки комментария с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadCommentWithNonExistentId() {
        assertThat(commentService.findById(FIFTH_COMMENT_ID)).isEmpty();
    }

}
