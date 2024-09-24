package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    private final BookRepository bookRepository;

    public JpaCommentRepository(EntityManager em, BookRepository bookRepository) {
        this.em = em;
        this.bookRepository = bookRepository;
    }

    @Override
    public Optional<Comment> findById(long id) {
        var comment = em.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> findAllCommentsByBookId(long bookId) {
        EntityGraph<?> entityGraphComment = em.getEntityGraph("comment-book-entity-graph");
        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment c WHERE c.book = :book", Comment.class);
        query.setHint(FETCH.getKey(), entityGraphComment);
        query.setParameter("book", new Book(bookId));
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0L) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        var query = em.createQuery("DELETE FROM Comment c WHERE c.id = :id");
        query.setParameter("id", id);
        var countChanges = query.executeUpdate();
        if (countChanges == 0) {
            throw new EntityNotFoundException("Not a single comment was deleted, comment with id = %d does not exist"
                    .formatted(id));
        }
    }

}
