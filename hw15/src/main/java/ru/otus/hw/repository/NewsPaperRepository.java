package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.domain.NewsPaper;

public interface NewsPaperRepository extends MongoRepository<NewsPaper, String> {
}
