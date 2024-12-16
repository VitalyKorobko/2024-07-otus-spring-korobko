package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.domain.Writer;

public interface WriterRepository extends MongoRepository<Writer, String> {


}
