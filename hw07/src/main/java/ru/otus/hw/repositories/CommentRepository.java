package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(long id);

    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId")
    List<Comment> findAllByBookId(@Param("bookId") long bookId);

    void deleteById(long id);
}
