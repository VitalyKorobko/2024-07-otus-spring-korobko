package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthorController.class})
@Import({SecurityConfiguration.class})
public class AuthorControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    @Test
    @WithMockUser
    @DisplayName("должен возвращать страницу со списком всех жанров")
    void shouldReturnCorrectGenresList() throws Exception {
        List<AuthorDto> authorDtoList = List.of(
                new AuthorDto(
                        1,
                        "fullName_111"
                )
        );
        given(authorService.findAll()).willReturn(authorDtoList);
        var result = mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authorDtoList))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("fullName_111");
    }

}
