package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.AuthorMapper;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами ")
@DataJpaTest
@Import({AuthorServiceImpl.class, AuthorMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class AuthorServiceTest {
    @Autowired
    AuthorServiceImpl service;

    @DisplayName(" должен возвращать список AuthorsDto")
    @Test
    void shouldFindAllAuthors() {
        List<AuthorDto> authors = service.findAll();
        assertThat(authors).hasSize(3)
                .allMatch(Objects::nonNull)
                .allMatch(authorDto -> authorDto.getId() != 0 && !authorDto.getFullName().isEmpty());
    }
}
