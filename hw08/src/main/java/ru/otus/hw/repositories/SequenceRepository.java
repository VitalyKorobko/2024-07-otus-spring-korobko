package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.CustomSequence;

public interface SequenceRepository extends MongoRepository<CustomSequence, String> {
}
