package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.GenreMongoDto;

import java.util.List;
import java.util.Set;

public interface MongoGenreRepository extends MongoRepository<GenreMongoDto, String> {

    List<GenreMongoDto> findAllByNameIn(Set<String> ids);
}
