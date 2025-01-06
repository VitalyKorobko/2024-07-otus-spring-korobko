package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.AuthorMongoDto;

import java.util.List;

public interface MongoAuthorRepository extends MongoRepository<AuthorMongoDto, String> {

    List<AuthorMongoDto> findByFullName(String fullName);

}
