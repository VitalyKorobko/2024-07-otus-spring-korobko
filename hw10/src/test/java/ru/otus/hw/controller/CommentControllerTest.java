package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({CommentController.class, CommentMapper.class})
public class CommentControllerTest {
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
    @DisplayName("должен корректно сохранять новый комментарий")
    void shouldCorrectSaveNewComment() throws Exception {
        var expectedCommentDto = new CommentDto(1, "text_1", 1);
        given(commentService.insert(expectedCommentDto.getText(), expectedCommentDto.getBookId()))
                .willReturn(expectedCommentDto);
        String requestBody = mapper.writeValueAsString(expectedCommentDto);

        mvc.perform(post("/api/v1/comment/new").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedCommentDto)));
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

        mvc.perform(patch("/api/v1/comment").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedCommentDto)));
    }

    @Test
    @DisplayName("Должен корректно удалять комментарий")
    void shouldCorrectDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/comment/1"))
                .andExpect(status().isOk());
        verify(commentService, times(1)).deleteById(1L);
    }

}
