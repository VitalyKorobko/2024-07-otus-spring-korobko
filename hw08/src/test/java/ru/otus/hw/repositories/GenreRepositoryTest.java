package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoRepository для работы с жанрами книг ")
@DataMongoTest
public class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("должен загружать жанры по списку ids жанров")
    @Test
    void shouldReturnCorrectGenreListByIds() {
        var actualGenres = genreRepository.findAllByIdIn(Set.of("1", "2", "3"));
        var expectedGenres = getDbGenres(1, 4);
        assertThat(actualGenres).
                isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = getDbGenres();
        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres(long start, long end) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
                .toList();
    }

}


