package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public JpaGenreRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Genre> findAll() {
        var query = entityManager.createQuery("SELECT g FROM Genre g", Genre.class);
        return query.getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var query = entityManager.createQuery("SELECT g FROM Genre g WHERE id IN (:ids)", Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

}
