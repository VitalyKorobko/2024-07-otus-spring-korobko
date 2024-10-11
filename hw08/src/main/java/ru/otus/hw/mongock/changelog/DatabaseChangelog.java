package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@ChangeLog(order = "001")
public class DatabaseChangelog {
    private Book book1;

    private Author author1, author2, author3;

    private Genre genre1, genre2, genre3, genre4, genre5, genre6;

    @ChangeSet(order = "000", id = "2024-09-17-001-dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
//        db.createCollection("author");
//        db.createCollection("genre");
//        db.createCollection("book");
//        db.createCollection("comment");
    }

    @ChangeSet(order = "001", id = "2024-09-17-001-authors", author = "korobko")
    public void insertAuthors(AuthorRepository repository) {
        author1 = repository.save(getAuthor(1L));
        author2 = repository.save(getAuthor(2L));
        author3 = repository.save(getAuthor(3L));
    }

    @ChangeSet(order = "002", id = "2024-09-17-001-genres", author = "korobko")
    public void insertGenres(GenreRepository repository) {
        genre1 = repository.save(getGenre(1L));
        genre2 = repository.save(getGenre(2L));
        genre3 = repository.save(getGenre(3L));
        genre4 = repository.save(getGenre(4L));
        genre5 = repository.save(getGenre(5L));
        genre6 = repository.save(getGenre(6L));
    }

    @ChangeSet(order = "003", id = "2024-09-17-001-books", author = "korobko")
    public void insertBooks(BookRepository repository) {
        book1 = repository.save(getBook(1L, author1, genre1, genre2));
        repository.save(getBook(2L, author2, genre3, genre4));
        repository.save(getBook(3L, author3, genre5, genre6));
    }

    @ChangeSet(order = "004", id = "2024-09-22-001-comments", author = "korobko")
    public void insertComments(CommentRepository repository) {
        var comment1 = repository.save(getComment(1L, book1));
        var comment2 = repository.save(getComment(2L, book1));
        var comment3 = repository.save(getComment(3L, book1));
    }

    private Comment getComment(long id, Book book) {
        return new Comment(
                id,
                "Comment_" + id,
                book
        );
    }

    private Book getBook(long id, Author author, Genre... genres) {
        return new Book(
                id,
                "BookTitle_" + id,
                author,
                List.of(genres)
        );
    }

    private Genre getGenre(long id) {
        return new Genre(id, "Genre_" + id);
    }

    private Author getAuthor(long id) {
        return new Author(id, "Author_" + id);
    }


}
