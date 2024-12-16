package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.VocabularyDao;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.Writer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.out;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@Slf4j
public class WriterServiceImpl implements WriterService {
    private static final String[] NAMES = {"Ivanov", "Petrov", "Sidorov"};

    private static final short MIN_COUNT_ARTICLES = 5;

    private static final short MAX_COUNT_ARTICLES = 10;

    private final ArticleGateway articleGateway;

    private final NewsPaperGateway newsPaperGateway;

    private final VocabularyDao vocabularyDao;

    public WriterServiceImpl(ArticleGateway articleGateway, NewsPaperGateway newsPaperGateway, VocabularyDao vocabularyDao) {
        this.articleGateway = articleGateway;
        this.newsPaperGateway = newsPaperGateway;
        this.vocabularyDao = vocabularyDao;
    }

    public String getAnyWriterName() {
        return NAMES[new Random().nextInt(0, NAMES.length)];
    }

    @Override
    public Writer create(String writerName) {
        return new Writer(
                null,
                writerName,
                vocabularyDao.findAll(writerName + ".csv").toArray(String[]::new)
        );
    }

    @Override
    public void startWriterLoop(int countNewsPapers) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < countNewsPapers; i++) {
            pool.execute(() -> {
                        out.println("\n\n=========================================\n\n");
                        var articles = createArticles();
                        newsPaperGateway.create(articles);

                    }
            );
            delay();
        }
        pool.shutdown();
    }

    private List<Article> createArticles() {
        var countArticlesInNewsPaper = new Random().nextInt(MIN_COUNT_ARTICLES, MAX_COUNT_ARTICLES);
        return IntStream.range(0, countArticlesInNewsPaper)
                .mapToObj((n) -> articleGateway.create(getAnyWriterName()))
                .collect(Collectors.toList());
    }

    private void delay() {
        try {
            SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
