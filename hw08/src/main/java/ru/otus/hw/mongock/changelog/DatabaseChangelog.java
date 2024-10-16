package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.enums.Seq;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.CustomSequence;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.CustomSequenceRepository;

import java.util.List;

@ChangeLog(order = "001")
public class DatabaseChangelog {
    private static final String COMMENT_SEQ_ID = "commentId";

    private Book book1;

    private Author author1;

    private Author author2;

    private Author author3;

    private Genre genre1;

    private Genre genre2;

    private Genre genre3;

    private Genre genre4;

    private Genre genre5;

    private Genre genre6;

    @ChangeSet(order = "000", id = "dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "001", id = "2024-10-15-001-authors", author = "korobko")
    public void insertAuthors(AuthorRepository repository) {
        author1 = repository.save(getAuthor(1L));
        author2 = repository.save(getAuthor(2L));
        author3 = repository.save(getAuthor(3L));
    }

    @ChangeSet(order = "002", id = "2024-10-15-002-genres", author = "korobko")
    public void insertGenres(GenreRepository repository) {
        genre1 = repository.save(getGenre(1L));
        genre2 = repository.save(getGenre(2L));
        genre3 = repository.save(getGenre(3L));
        genre4 = repository.save(getGenre(4L));
        genre5 = repository.save(getGenre(5L));
        genre6 = repository.save(getGenre(6L));
    }

    @ChangeSet(order = "003", id = "2024-10-15-003-books", author = "korobko")
    public void insertBooks(BookRepository repository, CustomSequenceRepository customSequenceRepository) {
        book1 = repository.save(getBook(1L, author1, genre1, genre2));
        repository.save(getBook(2L, author2, genre3, genre4));
        repository.save(getBook(3L, author3, genre5, genre6));
        customSequenceRepository.insert(new CustomSequence(Seq.BOOK.getSeqName(), 3L));
    }

    @ChangeSet(order = "004", id = "2024-10-15-004-comments", author = "korobko")
    public void insertComments(CommentRepository repository, CustomSequenceRepository customSequenceRepository) {
        repository.save(getComment(1L, book1));
        repository.save(getComment(2L, book1));
        repository.save(getComment(3L, book1));
        customSequenceRepository.insert(new CustomSequence(Seq.COMMENT.getSeqName(), 3L));
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
