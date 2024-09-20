package ru.otus.hw.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {
    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcBookRepository(GenreRepository genreRepository,
                              NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.genreRepository = genreRepository;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Optional<Book> findById(long id) {
        try {
            var book = namedParameterJdbcOperations.query("""
                            SELECT a.id, a.title, a.author_id, b.full_name, c.genre_id, d.name  
                            FROM (SELECT id, title, author_id 
                                  FROM books 
                                  WHERE id = :id) a 
                            LEFT JOIN authors b 
                                ON a.author_id  = b.id  
                            LEFT JOIN books_genres  c 
                                ON a.id  = c.book_id  
                            LEFT JOIN genres d 
                                ON c.genre_id  = d.id
                            """,
                    Map.of("id", id),
                    new BookResultSetExtractor()
            );
            return Optional.ofNullable(book);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("book with id = %d not exist ".formatted(id));
        }
    }

    @Override
    public List<Book> findAll() {
        var genreMap = convertToMapGenres(genreRepository.findAll());
        var bookMap = convertToMapBooks(getAllBooksWithoutGenres());
        var relations = getAllGenreRelations();
        return mergeBooksInfo(bookMap, genreMap, relations);
    }

    private List<Book> mergeBooksInfo(Map<Long, Book> bookMap, Map<Long, Genre> genreMap,
                                      List<BookGenreRelation> relations) {
        for (BookGenreRelation relation : relations) {
            bookMap
                    .get(relation.bookId())
                    .getGenres()
                    .add(genreMap.get(relation.genreId));
        }
        return bookMap.values().stream().toList();
    }

    private Map<Long, Genre> convertToMapGenres(List<Genre> genres) {
        return genres.stream()
                .parallel()
                .collect(
                        HashMap::new,
                        (map, genre) -> map.put(genre.getId(), genre),
                        HashMap::putAll
                );
    }

    private Map<Long, Book> convertToMapBooks(List<Book> books) {
        return books.stream()
                .parallel()
                .collect(
                        HashMap::new,
                        (map, book) -> map.put(book.getId(), book),
                        HashMap::putAll
                );
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var countChanges = namedParameterJdbcOperations.update(
                "DELETE FROM books WHERE id = :book_id",
                Map.of("book_id", id)
        );
        if (countChanges == 0) {
            throw new EntityNotFoundException("Not a single book was deleted, book with id = %d does not exist"
                    .formatted(id));
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query("""
                        SELECT 
                            a.id, 
                            a.title, 
                            a.author_id, 
                            b.full_name 
                        FROM books a 
                        LEFT JOIN authors b 
                            ON a.author_id = b.id
                        """,
                new BookRowMapper()
        );
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query(
                "SELECT book_id, genre_id FROM books_genres",
                new RelationsRowMapper()
        );
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("""
                        INSERT INTO books 
                            (
                            title, 
                            author_id
                            ) 
                        VALUES
                            (
                            :title, 
                            :author_id
                            )
                        """,
                new MapSqlParameterSource()
                        .addValue("title", book.getTitle())
                        .addValue("author_id", book.getAuthor().getId()),
                keyHolder,
                new String[]{"id"}
        );
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var countChanges = namedParameterJdbcOperations.update("""
                        UPDATE books 
                        SET 
                            title = :title, 
                            author_id = :author_id 
                        WHERE id = :id
                        """,
                Map.of(
                        "id", book.getId(),
                        "title", book.getTitle(),
                        "author_id", book.getAuthor().getId()
                )
        );
        if (countChanges == 0) {
            throw new EntityNotFoundException("No one book has been changed, book with id = %d does not exist"
                    .formatted(book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        namedParameterJdbcOperations.batchUpdate(
                "INSERT INTO books_genres(book_id, genre_id) VALUES(:book_id, :genre_id)",
                getParameters(book)
        );
    }

    private Map<String, Long>[] getParameters(Book book) {
        return book.getGenres().stream()
                .map(genre -> Map.of("book_id", book.getId(), "genre_id", genre.getId()))
                .toArray(Map[]::new);
    }

    private void removeGenresRelationsFor(Book book) {
        namedParameterJdbcOperations.update(
                "DELETE FROM books_genres WHERE book_id = :book_id",
                Map.of("book_id", book.getId())
        );
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    new Author(
                            rs.getLong("author_id"),
                            rs.getString("full_name")
                    ),
                    new ArrayList<>()
            );
        }

    }

    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            rs.next();
            return new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    getAuthor(rs),
                    getGenres(rs)
            );
        }

        private Author getAuthor(ResultSet rs) throws SQLException {
            return new Author(
                    rs.getLong("author_id"),
                    rs.getString("full_name")
            );
        }

        private List<Genre> getGenres(ResultSet rs) throws SQLException {
            List<Genre> genres = new ArrayList<>();
            genres.add(getGenre(rs));
            while (rs.next()) {
                genres.add(getGenre(rs));
            }
            return genres;
        }

        private Genre getGenre(ResultSet rs) throws SQLException {
            return new Genre(
                    rs.getLong("genre_id"),
                    rs.getString("name")
            );
        }

    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class RelationsRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(
                    rs.getLong("book_id"),
                    rs.getLong("genre_id")
            );
        }
    }

}
