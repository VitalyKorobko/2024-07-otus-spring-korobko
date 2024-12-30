package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с авторами книг ")
@DataJpaTest
public class AuthorRepositoryTest {
    private static final long FIRST_AUTHOR_ID = 1L;

    private static final String AUTHOR_FULL_NAME = "Author_1";

    @Autowired
    private AuthorRepository repositoryJpa;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var optionalActualAuthor = repositoryJpa.findById(FIRST_AUTHOR_ID);
        var expectedAuthor = entityManager.find(Author.class, FIRST_AUTHOR_ID);
        assertThat(optionalActualAuthor).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать автора по FullName")
    @Test
    void shouldReturnCorrectAuthorByFullName() {
        var actualAuthor = repositoryJpa.findByFullName(AUTHOR_FULL_NAME).get(0);
        var expectedAuthor = entityManager.find(Author.class, FIRST_AUTHOR_ID);
        assertThat(actualAuthor).isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repositoryJpa.findAll();
        var expectedAuthors = getDbAuthors();

        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
        actualAuthors.forEach(System.out::println);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }


}
