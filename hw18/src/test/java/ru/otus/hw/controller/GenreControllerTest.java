package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.exceptionhandler.ErrorResponse;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private GenreService genreService;

    @Test
    @DisplayName("должен возвращать список всех жанров")
    void shouldReturnCorrectGenresList() throws Exception {
        List<GenreDto> genreDtoList = List.of(
                new GenreDto(1, "genre_1"),
                new GenreDto(2, "genre_2")
        );
        given(genreService.findAll()).willReturn(genreDtoList);

        mvc.perform(get("/api/v1/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(genreDtoList)));
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если сработал CircuitBreaker при попытке получить список всех жанров")
    void shouldReturnErrorResponseIfCircuitBreakerOpen() throws Exception {
        when(genreService.findAll()).thenThrow(new RuntimeException());
        mvc.perform(get("/api/v1/genres"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(NOT_AVAILABLE_MESSAGE))));
    }


}
