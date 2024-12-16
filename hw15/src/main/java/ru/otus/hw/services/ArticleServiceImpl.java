package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.Writer;
import ru.otus.hw.repository.ArticleRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private static int count = 0;

    private static final short MIN_WORD_COUNT_IN_ARTICLE = 20;

    private static final short MAX_WORD_COUNT_IN_ARTICLE = 50;

    private static final short MIN_WORD_LENGTH = 1;

    private static final short MAX_WORD_LENGTH = 10;

    private final ArticleRepository repository;

    public ArticleServiceImpl(ArticleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Article create(Writer writer) {
        String[] words = writer.vocabulary();
        List<String> articleText = Arrays.stream(words)
                .filter(getFilterRule())
                .collect(Collectors.toList())
                .subList(0, new Random().nextInt(MIN_WORD_COUNT_IN_ARTICLE, MAX_WORD_COUNT_IN_ARTICLE));
        Article article = new Article(
                null,
                "number %d".formatted(count++),
                writer,
                articleText
        );
        log.info("New article {} have been created", article);
        return article;
    }

    private Predicate<String> getFilterRule() {
        return (w) -> w.length() > new Random().nextInt(MIN_WORD_LENGTH, MAX_WORD_LENGTH);
    }

    @Override
    public Article save(Article article) {
        var savedArticle = repository.save(article);
        log.info("SAVE Article {}", savedArticle);
        return savedArticle;
    }
}
