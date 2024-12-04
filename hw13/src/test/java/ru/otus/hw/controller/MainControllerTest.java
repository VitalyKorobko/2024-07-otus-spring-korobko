package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.assertj.core.api.Assertions.assertThat;


@WebMvcTest({MainController.class, BookMapper.class})
@Import({SecurityConfiguration.class})
public class MainControllerTest {
    private static final String AUTHORISATION = "Авторизация";

    private static final String REMEMBER_LOGIN_PASSWORD = "Запомнить логин и пароль";

    private static final String ERROR_MESSAGE = "Логин или пароль введены неверно";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("должен возвращать страницу авторизации")
    void shouldReturnAuthorizationPage() throws Exception {
        var result = mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains(AUTHORISATION).contains(REMEMBER_LOGIN_PASSWORD);
    }

    @Test
    @DisplayName("должен возвращать сообщение об ошибке на странице авторизации")
    void shouldReturnErrorMessage() throws Exception {
        var result = mvc.perform(get("/login?error=username"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", ERROR_MESSAGE))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains(ERROR_MESSAGE);
    }


    @Test
    @WithMockUser
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

