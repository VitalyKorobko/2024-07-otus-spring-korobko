package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.CommentMongoDto;

public interface MongoCommentRepository extends MongoRepository<CommentMongoDto, String> {
}
