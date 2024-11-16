package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.exceptionhandler.ErrorResponse;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentDtoWeb;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.CommentService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({CommentController.class, CommentMapper.class})
public class CommentControllerTest {
    private static final String MESSAGE = "Не найдено! Комментарий с id = 1 не найден";

    private static final String SERVER_ERROR_MESSAGE = "Server error! The request could not be completed.";

    private static final String VALIDATE_TEXT_MESSAGE = "Длина комментария должна быть не менее 5 символов";

    private static final String VALIDATE_BOOK_ID_MESSAGE = "Выберите книгу";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommentMapper commentMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("Должен возвращать корректный список комментариев по id книги")
    void shouldReturnCorrectCommentsListByBookId() throws Exception {
        List<CommentDto> commentDtoList = List.of(
                new CommentDto(1, "text_1", 1),
                new CommentDto(2, "text_2", 2)
        );
        given(commentService.findAllCommentsByBookId(1)).willReturn(commentDtoList);

        mvc.perform(get("/api/v1/comment").param("book_id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDtoList)));
    }

    @Test
    @DisplayName("должен возвращать корректный комментарий по id")
    void shouldReturnCorrectCommentById() throws Exception {
        var commentDto = new CommentDto(1, "text_1", 1);
        given(commentService.findById(1)).willReturn(Optional.of(commentDto));

        mvc.perform(get("/api/v1/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если комментария с запрашиваемым id не существует")
    void shouldReturnErrorResponseIfCommentNotExist() throws Exception {
        mvc.perform(get("/api/v1/comment/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(MESSAGE))));
    }

    @Test
    @DisplayName("должен корректно сохранять новый комментарий")
    void shouldCorrectSaveNewComment() throws Exception {
        var expectedCommentDto = new CommentDto(1, "text_1", 1);
        given(commentService.insert(expectedCommentDto.getText(), expectedCommentDto.getBookId()))
                .willReturn(expectedCommentDto);
        String requestBody = mapper.writeValueAsString(expectedCommentDto);

        mvc.perform(post("/api/v1/comment").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(expectedCommentDto)));
    }

    @Test
    @DisplayName("должен возврашать CommentDtoWeb с не пустым комментарием, если валидация не проходит по полю text, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByTextFieldWhenUsingPostMethod() throws Exception {
        var result = mvc.perform(post("/api/v1/comment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto(0, "", 1))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(result).contains(VALIDATE_TEXT_MESSAGE);
    }

    @Test
    @DisplayName("должен возврашать CommentDtoWeb с не пустым комментарием, если валидация не проходит по полю bookId")
    void shouldReturnNotNullMessageIfValidateNotCorrectByBookIdFieldWhenUsingPostMethod() throws Exception {
        var result = mvc.perform(post("/api/v1/comment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto(0, "comment", 0L))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(result).contains(VALIDATE_BOOK_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если переданы неверные данные в форму для создания нового комментария")
    void shouldReturnErrorResponseIfCommentDtoByNewBookIsNotCorrect() throws Exception {
        var text = "some text";
        var bookId = 1L;
        given(commentService.insert(text, bookId)).willThrow(new RuntimeException());
        mvc.perform(post("/api/v1/comment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDtoWeb(0, text, bookId, any()))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(SERVER_ERROR_MESSAGE))));
    }

    @Test
    @DisplayName("должен корректно обновлять комментарий")
    void shouldCorrectUpdateComment() throws Exception {
        var commentDto = new CommentDto(1, "text_1", 1);
        var expectedCommentDto = new CommentDto(1, "text_2", 2);

        given(commentService.findById(1)).willReturn(Optional.of(commentDto));
        given(commentService.update(
                expectedCommentDto.getId(),
                expectedCommentDto.getText(),
                expectedCommentDto.getBookId())
        ).willReturn(expectedCommentDto);

        String requestBody = mapper.writeValueAsString(expectedCommentDto);

        mvc.perform(patch("/api/v1/comment/1").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedCommentDto)));
    }

    @Test
    @DisplayName("должен возврашать CommentDtoWeb с не пустым комментарием, если валидация не проходит по полю text, Patch метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByTextFieldWhenUsingPatchMethod() throws Exception {
        var result = mvc.perform(patch("/api/v1/comment/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto(1, "", 1))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(result).contains(VALIDATE_TEXT_MESSAGE);
    }

    @Test
    @DisplayName("должен возврашать CommentDtoWeb с не пустым комментарием, если валидация не проходит по полю bookId, Patch метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByBookIdFieldWhenUsingPatchMethod() throws Exception {
        var result = mvc.perform(patch("/api/v1/comment/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto(1, "comment", 0L))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(result).contains(VALIDATE_BOOK_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если переданы неверные данные в форму для редактирования комментария")
    void shouldReturnErrorResponseIfBookDtoWebByExistBookIsNotCorrect() throws Exception {
        var commentId = 1L;
        var text = "some text";
        var bookId = 1L;
        given(commentService.update(commentId, text, bookId)).willThrow(new RuntimeException());
        mvc.perform(patch("/api/v1/comment/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDtoWeb(commentId, text, bookId, any()))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(SERVER_ERROR_MESSAGE))));
    }

    @Test
    @DisplayName("Должен корректно удалять комментарий")
    void shouldCorrectDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/comment/1"))
                .andExpect(status().isOk());
        verify(commentService, times(1)).deleteById(1L);
    }


}
