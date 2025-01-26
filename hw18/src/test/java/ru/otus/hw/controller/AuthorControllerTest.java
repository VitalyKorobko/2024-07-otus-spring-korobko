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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTest {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать корректный список всех авторов")
    void shouldReturnCorrectAuthorsList() throws Exception {
        List<AuthorDto> authorDtoList = List.of(
                new AuthorDto(
                        1,
                        "Ivanov")
        );
        given(authorService.findAll()).willReturn(authorDtoList);

        mvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authorDtoList)));
    }

    @Test
    @DisplayName("должен возвращать ErrorResponse, если сработал CircuitBreaker при попытке получить список всех авторов")
    void shouldReturnErrorResponseIfCircuitBreakerOpen() throws Exception {
        when(authorService.findAll()).thenThrow(new RuntimeException());
        mvc.perform(get("/api/v1/authors"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorResponse(NOT_AVAILABLE_MESSAGE))));
    }
}