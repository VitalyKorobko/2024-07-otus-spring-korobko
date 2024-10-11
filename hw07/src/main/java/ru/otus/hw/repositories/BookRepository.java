package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(value = "book-entity-graph")
    Optional<Book> findById(long id);

    @Override
    @EntityGraph(value = "book-entity-graph")
    List<Book> findAll();

}
