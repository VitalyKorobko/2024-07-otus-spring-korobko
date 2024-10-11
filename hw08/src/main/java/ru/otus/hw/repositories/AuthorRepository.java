package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.Optional;

public interface AuthorRepository extends MongoRepository<Author, Long> {

}
