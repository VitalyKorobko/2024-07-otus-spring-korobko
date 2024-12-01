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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({GenreController.class})
@Import({SecurityConfiguration.class})
public class GenreControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("должен возвращать страницу со списком всех жанров")
    void shouldReturnCorrectGenresList() throws Exception {
        List<GenreDto> genreDtoList = List.of(
                new GenreDto(
                        1,
                        "genre_111"
                )
        );
        given(genreService.findAll()).willReturn(genreDtoList);
        var result = mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("genres", genreDtoList))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("genre_111");
    }

}
