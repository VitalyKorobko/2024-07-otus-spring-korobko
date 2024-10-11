package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@ChangeLog(order = "001")
public class DatabaseChangelog {
    private Book book1;

    private Book book2;

    private Book book3;
//    --liquibase formatted sql
//
//--changeset korobko:2024-07-17-0001-authors
//    insert into authors(full_name)
//    values ('Author_1'), ('Author_2'), ('Author_3');
//
//--changeset korobko:2024-07-17-0001-genres
//    insert into genres(name)
//    values ('Genre_1'), ('Genre_2'), ('Genre_3'),
//            ('Genre_4'), ('Genre_5'), ('Genre_6');
//
//--changeset korobko:2024-07-17-0001-books
//    insert into books(title, author_id)
//    values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);
//
//--changeset korobko:2024-07-17-0001-books_genres
//    insert into books_genres(book_id, genre_id)
//    values (1, 1),   (1, 2),
//            (2, 3),   (2, 4),
//            (3, 5),   (3, 6);

//    --liquibase formatted sql
//
//--changeset korobko:2024-09-22-0001-comments
//    insert into comments(text, book_id)
//    values ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 1);


    @ChangeSet(order = "000", id = "2024-09-17-001-dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
        db.createCollection("books");
        db.createCollection("comments");
    }

    @ChangeSet(order = "001", id = "2024-09-17-001-books", author = "korobko", runAlways = true)
    public void insertBooks(BookRepository repository) {
        var author1 = getAuthor(1L);
        var author2 = getAuthor(2L);
        var author3 = getAuthor(3L);
        var genre1 = getGenre(1L);
        var genre2 = getGenre(2L);
        var genre3 = getGenre(3L);
        var genre4 = getGenre(4L);
        var genre5 = getGenre(5L);
        var genre6 = getGenre(6L);
        book1 = repository.save(getBook(1L, author1, genre1, genre2));
        book2 = repository.save(getBook(2L, author2, genre3, genre4));
        book3 = repository.save(getBook(3L, author3, genre5, genre6));
    }

//    @ChangeSet(order = "002", id = "2024-09-22-001-comments", author = "korobko", runAlways = true)
//    public void insertComments(MongoDatabase db) {
//        MongoCollection<Document> commentCollection = db.getCollection("comments");
//        var comment1 = getCommentDocument(1L, 1L);
//        var comment2 = getCommentDocument(2L, 1L);
//        var comment3 = getCommentDocument(3L, 1L);
//        commentCollection.insertOne(comment1);
//        commentCollection.insertOne(comment2);
//        commentCollection.insertOne(comment3);
//    }

    @ChangeSet(order = "002", id = "2024-09-22-001-comments", author = "korobko", runAlways = true)
    public void insertComments(CommentRepository repository) {
        var comment1 = repository.save(getComment(1L, book1));
        var comment2 = repository.save(getComment(2L, book1));
        var comment3 = repository.save(getComment(3L, book1));
    }

    //    private Document getCommentDocument(long id, long bookId) {
//        return new Document()
//                .append("id", id)
//                .append("text", "Comment_" + id)
//                .append("bookId", bookId);
//    }

    private Comment getComment(long id, Book book) {
        return new Comment(
                id,
                "Comment_" + id,
                book
        );
    }

//    private Document getBookDocument(long id, Author author, Genre... genres) {
//        return new Document()
//                .append("id", id)
//                .append("title", "BookTitle_" + id)
//                .append("author", author)
//                .append("genres", genres);
//    }

    private Book getBook(long id, Author author, Genre... genres) {
        return new Book(
                id,
                "BookTitle_" + id,
                author,
                List.of(genres)
        );
    }

//    private Document getGenreDocument(long id) {
//        return new Document()
//                .append("id", id)
//                .append("name", "Genre_" + id);
//    }

    private Genre getGenre(long id) {
        return new Genre(id, "Genre_" + id);
    }

//    private Document getAuthorDocument(long id) {
//        return new Document()
//                .append("id", id)
//                .append("fullName", "Author_" + id);
//    }

    private Author getAuthor(long id) {
        return new Author(id, "Author_" + id);
    }


}


//
//@ChangeLog(order = "001")
//public class InitMongoDBDataChangeLog {
//
//    private Knowledge springDataKnowledge;
//    private Knowledge mongockKnowledge;
//    private Knowledge aggregationApiKnowledge;
//
//    @ChangeSet(order = "000", id = "dropDB", author = "stvort", runAlways = true)
//    public void dropDB(MongoDatabase database){
//        database.drop();
//    }
//
//    @ChangeSet(order = "001", id = "initKnowledges", author = "stvort", runAlways = true)
//    public void initKnowledges(KnowledgeRepository repository){
//        springDataKnowledge = repository.save(new Knowledge("Spring Data"));
//        mongockKnowledge = repository.save(new Knowledge("Mongock"));
//        aggregationApiKnowledge = repository.save(new Knowledge("AggregationApi"));
//    }
//
//    @ChangeSet(order = "002", id = "initStudents", author = "stvort", runAlways = true)
//    public void initStudents(StudentRepository repository){
//        repository.save(new Student("Student #1", springDataKnowledge, mongockKnowledge));
//    }
//
//    @ChangeSet(order = "003", id = "Teacher", author = "stvort", runAlways = true)
//    public void initTeachers(TeacherRepository repository){
//        val teacher = new Teacher("Teacher #1", springDataKnowledge, mongockKnowledge, aggregationApiKnowledge);
//        repository.save(teacher);
//    }
//}