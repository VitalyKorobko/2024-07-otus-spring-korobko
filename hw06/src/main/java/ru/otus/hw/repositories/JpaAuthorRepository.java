package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAuthorRepository implements AuthorRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public JpaAuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Author> findAll() {
        var query = entityManager.createQuery("SELECT a FROM Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        var author = entityManager.find(Author.class, id);
        return Optional.ofNullable(author);
    }

}
