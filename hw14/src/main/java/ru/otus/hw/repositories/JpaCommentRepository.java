package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBookId(long bookId);

}
