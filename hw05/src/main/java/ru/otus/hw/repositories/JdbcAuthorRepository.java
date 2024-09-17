package ru.otus.hw.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcAuthorRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query(
                "SELECT id, full_name FROM authors",
                new AuthorRowMapper()
        );
    }

    @Override
    public Optional<Author> findById(long id) {
        try {
            Author author = namedParameterJdbcOperations.queryForObject(
                    "SELECT id, full_name FROM authors WHERE id = :id",
                    Map.of("id", id),
                    new AuthorRowMapper()
            );
            return Optional.ofNullable(author);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Author with id %d not found".formatted(id));
        }

    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(
                    rs.getInt("id"),
                    rs.getString("full_name")
            );
        }
    }

}
