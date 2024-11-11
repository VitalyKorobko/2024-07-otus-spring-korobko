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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

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

        mvc.perform(get("/api/v1/book/all"))
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
    @DisplayName("должен корректно сохранять новую книгу")
    void shouldCorrectSaveNewBook() throws Exception {
        var expectedBookDto = new BookDto(
                1,
                "title_111",
                new AuthorDto(1, "author_111"),
                List.of(new GenreDto(1, "genre_111"))
        );
        var bookDtoWeb = bookMapper.toBookDtoWeb(expectedBookDto);
        given(bookService.insert(bookDtoWeb.getTitle(), bookDtoWeb.getAuthorId(), bookDtoWeb.getSetGenresId()))
                .willReturn(expectedBookDto);
        String requestBody = mapper.writeValueAsString(bookDtoWeb);

        mvc.perform(post("/api/v1/book/new").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookDto)));
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
        var bookDtoWeb = bookMapper.toBookDtoWeb(expectedBookDto);

        given(bookService.findById(1)).willReturn(Optional.of(bookDto));
        given(bookService.update(
                bookDtoWeb.getId(),
                bookDtoWeb.getTitle(),
                bookDtoWeb.getAuthorId(),
                bookDtoWeb.getSetGenresId())
        ).willReturn(expectedBookDto);

        String requestBody = mapper.writeValueAsString(bookDtoWeb);

        mvc.perform(patch("/api/v1/book").contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookDto)));
    }

    @Test
    @DisplayName("Должен корректно удалять книгу")
    void shouldCorrectDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isOk());
        verify(bookService, times(1)).deleteById(1L);
    }

}
