package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaBookRepository implements BookRepository {
    @PersistenceContext
    private final EntityManager em;

    public JpaBookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraphAuthor = em.getEntityGraph("book-author-entity-graph");
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(FETCH.getKey(), entityGraphAuthor);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0L) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var query = em.createQuery("""
                DELETE FROM Book b WHERE b.id = :id 
                """);
        query.setParameter("id", id);
        var countChanges = query.executeUpdate();
        if (countChanges == 0) {
            throw new EntityNotFoundException("Not a single book was deleted, book with id = %d does not exist"
                    .formatted(id));
        }
    }



}
