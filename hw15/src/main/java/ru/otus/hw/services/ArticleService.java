package ru.otus.hw.services;

import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.Writer;

import java.util.List;

public interface ArticleService {
    Article create(Writer writer);

    Article save(Article article);

    List<Article> findAll();

}
