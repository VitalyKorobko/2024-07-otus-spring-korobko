package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.domain.Article;

public interface ArticleRepository extends MongoRepository<Article, String> {
}
