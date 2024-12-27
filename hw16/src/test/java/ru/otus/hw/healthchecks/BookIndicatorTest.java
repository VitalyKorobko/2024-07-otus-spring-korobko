package ru.otus.hw.healthchecks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Health индикатор ")
public class BookIndicatorTest {
    private static final String POSITIVE_MESSAGE_TEMPLATE = "Количество книг %d";

    private static final String NEGATIVE_MESSAGE = "Не найдено ни одной книги!";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService service;

    @Test
    @DisplayName(" должен возвращать ожидаемое сообщение, если количество книг равно 0")
    void shouldReturnExpectedValueIfBookAmountEqual0() throws Exception {
        given(service.findAll()).willReturn(new ArrayList<>());
        var result = mvc.perform(get("/monitor/health/bookIndicator"))
                .andExpect(status().is(503))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(NEGATIVE_MESSAGE);
    }

    @Test
    @DisplayName(" должен возвращать сообщение с количеством книг")
    void shouldReturnExpectedValueIfBookAmountNotEqual0() throws Exception {
        List<BookDto> list = List.of(new BookDto(1L, "", null, null));
        given(service.findAll()).willReturn(list);
        var result = mvc.perform(get("/monitor/health/bookIndicator"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).contains(POSITIVE_MESSAGE_TEMPLATE.formatted(list.size()));
    }

}
