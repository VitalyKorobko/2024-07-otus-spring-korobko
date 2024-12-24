package ru.otus.hw.services;

import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;

import java.util.List;

public interface NewsPaperService {

    NewsPaper get(List<Article> articles);

    NewsPaper save(NewsPaper newsPaper);

    List<NewsPaper> findAll();

}
