package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;
import ru.otus.hw.repository.NewsPaperRepository;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class NewsPaperServiceImpl implements NewsPaperService {
    private final static String[] NAMES = {"BBC", "The New York Times", "Aif",
            "THE GUARDIAN", "Le Figaro", "TheTimes of India"};

    private final NewsPaperRepository repository;

    public NewsPaperServiceImpl(NewsPaperRepository repository) {
        this.repository = repository;
    }

    @Override
    public NewsPaper get(List<Article> articles) {
        var newsPaper = new NewsPaper(null, NAMES[new Random().nextInt(0, NAMES.length)], articles);
        log.info("NewsPaper {} have been created", newsPaper);
        return newsPaper;
    }

    @Override
    public NewsPaper save(NewsPaper newsPaper) {
        var savedNewsPaper = repository.save(newsPaper);
        log.info("SAVE NewsPaper: {}", savedNewsPaper);
        return savedNewsPaper;
    }

    @Override
    public List<NewsPaper> findAll() {
        log.info("SHOW All NEWS_PAPERS");
        return repository.findAll();
    }

}
