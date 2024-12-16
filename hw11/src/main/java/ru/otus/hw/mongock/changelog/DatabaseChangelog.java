package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@ChangeLog(order = "001")
public class DatabaseChangelog {
    private Book book1;

    @ChangeSet(order = "000", id = "dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "001", id = "2024-12-09-001-authors", author = "korobko")
    public void insertAuthors(AuthorRepository authorRepository) {
        var authors = Set.of(getAuthor(1L), getAuthor(2L), getAuthor(3L));
        authorRepository.saveAll(authors).blockLast();
    }

    @ChangeSet(order = "002", id = "2024-12-09-002-genres", author = "korobko")
    public void insertGenres(GenreRepository repository) {
        var genres = Set.of(getGenre(1L), getGenre(2L), getGenre(3L),
                getGenre(4L), getGenre(5L), getGenre(6L));
        repository.saveAll(genres).blockLast();
    }

    @ChangeSet(order = "003", id = "2024-12-09-003-books", author = "korobko")
    public void insertBooks(BookRepository repository) {
        book1 = repository.save(getBook(1L, getAuthor(1L), getGenre(1L), getGenre(2L))).block();
        var books = Set.of(getBook(2L, getAuthor(2L), getGenre(3L), getGenre(4L)),
                getBook(3L, getAuthor(3L), getGenre(5L), getGenre(6L))
        );
        repository.saveAll(books).blockLast();
    }

    @ChangeSet(order = "004", id = "2024-12-09-004-comments", author = "korobko")
    public void insertComments(CommentRepository repository) {
        var comments = Set.of(getComment(1L, book1),
                getComment(2L, book1),
                getComment(3L, book1)
        );
        repository.saveAll(comments).blockLast();
    }

    private Comment getComment(long id, Book book) {
        return new Comment(
                String.valueOf(id),
                "Comment_" + id,
                book
        );
    }

    private Book getBook(long id, Author author, Genre... genres) {
        return new Book(
                String.valueOf(id),
                "BookTitle_" + id,
                author,
                List.of(genres)
        );
    }

    private Genre getGenre(long id) {
        return new Genre(String.valueOf(id), "Genre_" + id);
    }

    private Author getAuthor(long id) {
        return new Author(String.valueOf(id), "Author_" + id);
    }


}
