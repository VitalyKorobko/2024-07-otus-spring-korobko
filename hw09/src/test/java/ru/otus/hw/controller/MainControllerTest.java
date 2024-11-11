package ru.otus.hw.controller;

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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.assertj.core.api.Assertions.assertThat;


@WebMvcTest({MainController.class, BookMapper.class})
public class MainControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

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


}
