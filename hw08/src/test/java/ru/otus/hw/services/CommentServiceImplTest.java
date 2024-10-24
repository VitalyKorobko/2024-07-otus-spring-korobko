package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.repositories.SequenceRepositoryCustomImpl;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями ")
@DataMongoTest
@Import({CommentServiceImpl.class, IdSequencesServiceImpl.class,
        SequenceRepositoryCustomImpl.class, CommentMapper.class})
public class CommentServiceImplTest {
    private static final String FIRST_BOOK_ID = "1";
    private static final String SECOND_BOOK_ID = "2";

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 3;

    private static final String FIRST_COMMENT_TEXT = "Comment_1";

    private static final String SECOND_COMMENT_TEXT = "Comment_2";

    private static final String THIRD_COMMENT_TEXT = "Comment_3";

    private static final String FIRST_COMMENT_ID = "1";

    private static final String SECOND_COMMENT_ID = "2";

    private static final String THIRD_COMMENT_ID = "3";

    private static final String FOURTH_COMMENT_ID = "4";

    private static final String FIFTH_COMMENT_ID = "5";

    @Autowired
    private CommentServiceImpl commentService;

    @DisplayName(" должен возвращать список комментариев по id книги")
    @Test
    void shouldFindAllCommentsById() {
        List<CommentDto> comments = commentService.findAllCommentsByBookId(FIRST_BOOK_ID);
        assertThat(comments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                .allMatch(Objects::nonNull)
                .allMatch(commentDto -> !commentDto.getId().equals("0") && !commentDto.getText().isEmpty())
                .matches(c -> Objects.equals(c.get(0).getId(), FIRST_COMMENT_ID)
                        && Objects.equals(c.get(0).getText(), FIRST_COMMENT_TEXT)
                        && Objects.equals(c.get(0).getBookId(), FIRST_BOOK_ID))
                .matches(c -> Objects.equals(c.get(1).getId(), SECOND_COMMENT_ID)
                        && Objects.equals(c.get(1).getText(), SECOND_COMMENT_TEXT)
                        && Objects.equals(c.get(1).getBookId(), FIRST_BOOK_ID))
                .matches(c -> Objects.equals(c.get(2).getId(), THIRD_COMMENT_ID)
                        && Objects.equals(c.get(2).getText(), THIRD_COMMENT_TEXT)
                        && Objects.equals(c.get(2).getBookId(), FIRST_BOOK_ID));
    }

    @DisplayName(" должен возвращать комментарий по id")
    @Test
    void shouldReturnCommentById() {
        var optionalCommentDto = commentService.findById(FIRST_COMMENT_ID);
        assertThat(optionalCommentDto).isPresent()
                .get()
                .matches(c -> Objects.equals(c.getId(), FIRST_COMMENT_ID)
                        && Objects.equals(c.getText(), FIRST_COMMENT_TEXT));
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
                .matches(c -> Objects.equals(c.getId(), FOURTH_COMMENT_ID)
                        && c.getText().equals(THIRD_COMMENT_TEXT))
                .matches(comment -> comment.getBookId().equals(SECOND_BOOK_ID));

    }

    @DisplayName(" должен сохранять измененный комментарий к книге")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveChangedComment() {
        var optionalCommentDto = commentService.findById(THIRD_COMMENT_ID);
        assertThat(optionalCommentDto).isPresent()
                .get()
                .matches(c -> Objects.equals(c.getId(), THIRD_COMMENT_ID)
                        && Objects.equals(c.getText(), THIRD_COMMENT_TEXT)
                        && Objects.equals(c.getBookId(), FIRST_BOOK_ID)
                );

        var returnedCommentDto = commentService.update(
                THIRD_COMMENT_ID,
                FIRST_COMMENT_TEXT,
                SECOND_BOOK_ID
        );

        assertThat(returnedCommentDto).isNotNull()
                .matches(c -> Objects.equals(c.getId(), THIRD_COMMENT_ID)
                        && Objects.equals(c.getText(), FIRST_COMMENT_TEXT))
                .matches(c -> Objects.equals(c.getBookId(), SECOND_BOOK_ID));
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
        Assertions.assertDoesNotThrow(() -> commentService.deleteById("5"));
    }

    @DisplayName(" должен возвращать пустой Optional при попытке загрузки комментария с несуществующим Id")
    @Test
    void shouldReturnEmptyOptionalWhenTryingToLoadCommentWithNonExistentId() {
        assertThat(commentService.findById(FIFTH_COMMENT_ID)).isEmpty();
    }

}
