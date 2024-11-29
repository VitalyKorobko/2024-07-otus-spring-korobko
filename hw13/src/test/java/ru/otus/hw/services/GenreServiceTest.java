package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mapper.GenreMapper;

import java.util.Objects;

@DisplayName("сервис для тестрования жанров")
@DataJpaTest
@Import({GenreServiceImpl.class, GenreMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class GenreServiceTest {

    @Autowired
    private GenreServiceImpl service;

    @DisplayName("должен возвращать корректный список жанров")
    @Test
    void shouldFindAllGenres() {
        assertThat(service.findAll()).hasSize(6)
                .allMatch(Objects::nonNull)
                .allMatch(g -> g.getId() != 0 && !g.getName().isEmpty());
    }

}
