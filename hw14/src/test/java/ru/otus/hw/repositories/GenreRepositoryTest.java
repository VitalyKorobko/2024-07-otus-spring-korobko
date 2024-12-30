package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами книг ")
@DataJpaTest
public class GenreRepositoryTest {
    @Autowired
    private GenreRepository repositoryJpa;

    @DisplayName("должен загружать жанры по списку ids жанров")
    @Test
    void shouldReturnCorrectGenreListByIds() {
        var actualGenres = repositoryJpa.findAllByIdIn(Set.of(1L, 2L, 3L));
        var expectedGenres = getDbGenres(1, 4);
        assertThat(actualGenres).
                isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать жанры по списку names жанров")
    @Test
    void shouldReturnCorrectGenreListByNames() {
        var actualGenres = repositoryJpa.findAllByNameIn(Set.of("Genre_1", "Genre_2", "Genre_3"));
        var expectedGenres = getDbGenres(1, 4);
        assertThat(actualGenres).
                isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenres = repositoryJpa.findAll();
        var expectedGenres = getDbGenres();

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
        actualGenres.forEach(System.out::println);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres(long start, long end) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }


}
