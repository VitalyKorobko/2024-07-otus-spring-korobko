package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.BookService;
import ru.otus.hw.controller.exceptionhandler.ErrorResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
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


@WebMvcTest({BookController.class, BookMapper.class})
public class BookControllerTest {
    private static final String MESSAGE = "Не найдено! Книга с id = 1 не найдена";

    private static final String SERVER_ERROR_MESSAGE = "Server error! The request could not be completed.";

    private static final String VALIDATE_TITLE_MESSAGE = "Поле название книги не может быть пустым";

    private static final String VALIDATE_AUTHOR_ID_MESSAGE = "Выберите автора книги";

    private static final String VALIDATE_SET_GENRES_ID_MESSAGE = "Выберите жанры для книги";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookMapper bookMapper;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("должен возвращать корректный список всех книг")
    void shouldReturnCorrectBooksList() throws Exception {
        List<BookDto> bookDtoList = List.of(
                new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111")))
        );
        given(bookService.findAll()).willReturn(bookDtoList);

        mvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDtoList)));
    }

    @Test
    @DisplayName("должен возвращать корректную книгу по id")
    void shouldReturnCorrectBookById() throws Exception {
        var bookDto = new BookDto(
                1,
                "title_111",
                new AuthorDto(1, "author_111"),
                List.of(new GenreDto(1, "genre_111"))
        );
        given(bookService.findById(1)).willReturn(Optional.of(bookDto));

        mvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если книги с запрашиваемым id не существует")
    void shouldReturnErrorResponseIfBookNotExist() throws Exception {
        mvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(MESSAGE))));
    }

    @Test
    @DisplayName("должен корректно сохранять новую книгу")
    void shouldCorrectSaveNewBook() throws Exception {
        var bookDto = new BookDto(
                1,
                "title_111",
                new AuthorDto(1, "author_111"),
                List.of(new GenreDto(1, "genre_111"))
        );
        var bookDtoWeb = bookMapper.toBookDtoWeb(bookDto, null);
        given(bookService.insert(bookDtoWeb.getTitle(), bookDtoWeb.getAuthorFullName(), bookDtoWeb.getSetGenreNames()))
                .willReturn(bookDto);
        String requestBody = mapper.writeValueAsString(bookDtoWeb);

        mvc.perform(post("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(bookDtoWeb)));
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю title, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByTitleFieldWhenUsingPostMethod() throws Exception {
        var result = mvc.perform(post("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(0L, "", "a", Set.of("g"), null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_TITLE_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю authorFullName, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByAuthorFullNameFieldWhenUsingPostMethod() throws Exception {
        var result = mvc.perform(post("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(0L, "title", "", Set.of("g"), null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_AUTHOR_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю setGenresId, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectBySetGenresIdFieldWhenUsingPostMethod() throws Exception {
        var result = mvc.perform(post("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(0L, "title", "a", new HashSet<>(), null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_SET_GENRES_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если переданы неверные данные в форму для создания новой книги")
    void shouldReturnErrorResponseIfBookDtoWebByNewBookIsNotCorrect() throws Exception {
        var title = "some title";
        var authorName = "author";
        var setGenres = Set.of("genre_1");
        given(bookService.insert(title, authorName, setGenres)).willThrow(new RuntimeException());
        mvc.perform(post("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(0, title, authorName, setGenres, any()))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(SERVER_ERROR_MESSAGE))));
    }

    @Test
    @DisplayName("должен корректно обновлять книгу")
    void shouldCorrectUpdateBook() throws Exception {
        var bookDto = new BookDto(
                1,
                "title_111",
                new AuthorDto(1, "author_111"),
                List.of(new GenreDto(1, "genre_111"))
        );
        var expectedBookDto = new BookDto(
                1,
                "title_222",
                new AuthorDto(2, "author_222"),
                List.of(new GenreDto(2, "genre_222"))
        );
        var bookDtoWeb = bookMapper.toBookDtoWeb(expectedBookDto, any());

        given(bookService.findById(1)).willReturn(Optional.of(bookDto));
        given(bookService.update(
                bookDtoWeb.getId(),
                bookDtoWeb.getTitle(),
                bookDtoWeb.getAuthorFullName(),
                bookDtoWeb.getSetGenreNames())
        ).willReturn(expectedBookDto);

        String requestBody = mapper.writeValueAsString(bookDtoWeb);

        mvc.perform(patch("/api/v1/book/1").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(requestBody));
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю title, Patch метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByTitleFieldWhenUsingPatchMethod() throws Exception {
        var result = mvc.perform(patch("/api/v1/book/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(1L, "", "a", Set.of("g"), null))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_TITLE_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю authorFullName, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectByFullNameFieldWhenUsingPatchMethod() throws Exception {
        var result = mvc.perform(patch("/api/v1/book/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(1L, "title", "", Set.of("g"), null))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_AUTHOR_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать BookDtoWeb с не пустым комментарием, если валидация не проходит по полю setGenresId, Post метод")
    void shouldReturnNotNullMessageIfValidateNotCorrectBySetGenresIdFieldWhenUsingPatchMethod() throws Exception {
        var result = mvc.perform(patch("/api/v1/book/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(1L, "title", "a", new HashSet<>(), null))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(VALIDATE_SET_GENRES_ID_MESSAGE);
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если переданы неверные данные в форму для редактирования книги")
    void shouldReturnErrorResponseIfBookDtoWebByExistBookIsNotCorrect() throws Exception {
        var title = "some title";
        var authorName = "author";
        var setGenres = Set.of("genre_1");
        given(bookService.update(1, title, authorName, setGenres)).willThrow(new RuntimeException());
        mvc.perform(patch("/api/v1/book/1").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookDtoWeb(1 , title, authorName, setGenres, any()))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(SERVER_ERROR_MESSAGE))));
    }

    @Test
    @DisplayName("Должен корректно удалять книгу")
    void shouldCorrectDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isOk());
        verify(bookService, times(1)).deleteById(1L);
    }

}
