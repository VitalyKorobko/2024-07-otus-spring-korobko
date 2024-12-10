package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {AuthorController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({AuthorRepository.class, AuthorMapper.class})
@EnableAutoConfiguration
public class AuthorControllerTest {
    @MockBean
    private AuthorRepository repository;

    @Autowired
    private AuthorMapper mapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("должен возвращать корректный список всех авторов")
    void shouldReturnCorrectAuthorsList() {
        var authorsDto = List.of(new AuthorDto("1", "Ivanov"),
                new AuthorDto("2", "Petrov"));

        var authorFlux = Flux.fromIterable(authorsDto);
        given(repository.findAll()).willReturn(authorFlux.map(mapper::toAuthor));

        var result = webTestClient
                .get().uri("/api/v1/authors")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = null;
        for (AuthorDto dto : authorsDto) {
            stepResult = step.expectNext(dto);
        }
        stepResult.verifyComplete();
    }

}
