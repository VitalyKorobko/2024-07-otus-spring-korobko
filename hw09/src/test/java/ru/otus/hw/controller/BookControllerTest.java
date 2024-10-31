package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.assertj.core.api.Assertions.assertThat;


@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать страницу со списком всех книг")
    void shouldReturnCorrectBooksList() throws Exception {
        List<BookDto> bookDtoList = List.of(
                new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111")))
        );
        given(bookService.findAll()).willReturn(bookDtoList);
        var result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("title_111")
                .contains("author_111")
                .contains("genre_111");
    }


    @Test
    @DisplayName("Должен возвращать страницу для редактирования книги")
    void shouldReturnEditBook() throws Exception {
        BookDto bookDto = new BookDto(
                1,
                "title_111",
                new AuthorDto(1, "author_111"),
                List.of(new GenreDto(1, "genre_111"))
        );
        List<AuthorDto> authorDtoList = List.of(new AuthorDto(1, "author_111"),
                new AuthorDto(2, "author_222"));
        List<GenreDto> genreDtoList = List.of(new GenreDto(1, "genre_111"),
                new GenreDto(2, "genre_222"));
        given(bookService.findById(1)).willReturn(Optional.of(bookDto));
        given(authorService.findAll()).willReturn(authorDtoList);
        given(genreService.findAll()).willReturn(genreDtoList);

        var result = mvc.perform(get("/book").param("num", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", bookDto))
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList))
                .andReturn().getResponse().getContentAsString();
        assertThat(result)
                .contains("title_111")
                .contains("author_111")
                .contains("author_222")
                .contains("genre_111")
                .contains("genre_222");
    }

    @Test
    @DisplayName("Должен возвращать страницу для добавления новой книги")
    void shouldReturnDataByNewBook() throws Exception {
        List<AuthorDto> authorDtoList = List.of(new AuthorDto(1, "author_111"));
        List<GenreDto> genreDtoList = List.of(new GenreDto(1, "genre_111"));

        given(authorService.findAll()).willReturn(authorDtoList);
        given(genreService.findAll()).willReturn(genreDtoList);

        var result = mvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", new BookDtoWeb()))
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("author_111").contains("genre_111");

    }

    @Test
    @DisplayName("Должен сохранять новую книгу и перенаправлять на страницу со списком книг")
    void shouldSaveNewBook() throws Exception {
        mvc.perform(post("/book/new")
                        .param("id", "1")
                        .param("title", "title_111")
                        .param("authorId", "1")
                        .param("setGenresId", "1", "2")
                )
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        verify(bookService, times(1)).insert("title_111", 1, Set.of(1L, 2L));

    }

    @Test
    @DisplayName("Не должен сохранять новую книгу, если проверка для названия книги пустое поле")
    void shouldNotSaveNewBook() throws Exception {
        List<AuthorDto> authorDtoList = List.of(new AuthorDto(1, "author_111"),
                new AuthorDto(2, "author_222"));
        List<GenreDto> genreDtoList = List.of(new GenreDto(1, "genre_111"),
                new GenreDto(2, "genre_222"));
        given(authorService.findAll()).willReturn(authorDtoList);
        given(genreService.findAll()).willReturn(genreDtoList);

        BookDtoWeb expectedBook = new BookDtoWeb(1, "", 1, Set.of(1L, 2L));

        String result = mvc.perform(post("/book/new")
                        .param("id", "1")
                        .param("title", "")
                        .param("authorId", "1")
                        .param("setGenresId", "1", "2")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList))
                .andExpect(model().attribute("book", expectedBook))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("author_111")
                .contains("author_222")
                .contains("genre_111")
                .contains("genre_222");
    }

    @Test
    @DisplayName("Должен изменять существующую книгу и перенаправлять на страницу со списком книг")
    void shouldSaveExistBook() throws Exception {
        mvc.perform(post("/book/1/update")
                        .param("id", "1")
                        .param("title", "title_222")
                        .param("authorId", "1")
                        .param("setGenresId", "1", "2")
                )
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        verify(bookService, times(1)).update(1, "title_222", 1, Set.of(1L, 2L));
    }

    @Test
    @DisplayName("Не должен сохранять существующую книгу, если проверка для названия книги пустое поле")
    void shouldNotSaveExistBook() throws Exception {
        List<AuthorDto> authorDtoList = List.of(new AuthorDto(1, "author_111"),
                new AuthorDto(2, "author_222"));
        List<GenreDto> genreDtoList = List.of(new GenreDto(1, "genre_111"),
                new GenreDto(2, "genre_222"));
        given(authorService.findAll()).willReturn(authorDtoList);
        given(genreService.findAll()).willReturn(genreDtoList);

        BookDtoWeb expectedBook = new BookDtoWeb(1, "", 1, Set.of(1L, 2L));

        String result = mvc.perform(post("/book/1/update")
                        .param("id", "1")
                        .param("title", "")
                        .param("authorId", "1")
                        .param("setGenresId", "1", "2")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList))
                .andExpect(model().attribute("book", expectedBook))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("author_111")
                .contains("author_222")
                .contains("genre_111")
                .contains("genre_222");
    }

    @Test
    @DisplayName("Должен удалять книгу")
    void shouldDeleteBook() throws Exception {
        mvc.perform(get("/book/del/1"))
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        verify(bookService, times(1)).deleteById(1L);
    }

}
