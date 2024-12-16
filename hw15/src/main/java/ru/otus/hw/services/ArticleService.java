package ru.otus.hw.services;

import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.Writer;

public interface ArticleService {
    Article create(Writer writer);

    Article save(Article article);

}
