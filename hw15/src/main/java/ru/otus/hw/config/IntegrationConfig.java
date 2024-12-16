package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;
import ru.otus.hw.services.ArticleService;
import ru.otus.hw.services.NewsPaperService;
import ru.otus.hw.services.WriterService;

import java.util.List;
import java.util.stream.Collectors;


@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public QueueChannelSpec writerChannel() {
        return MessageChannels.queue(5);
    }

    @Bean
    public PublishSubscribeChannelSpec<?> articleChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public QueueChannelSpec articlesChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public PublishSubscribeChannelSpec<?> newsPaperChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(1000));
        return pollerMetadata;
    }

    @Bean
    public IntegrationFlow articleFlow(ArticleService articleService, WriterService writerService) {
        return IntegrationFlow
                .from("writerChannel")
                .handle(writerService, "create")
                .handle(articleService, "create")
                .channel("articleChannel")
                .get();
    }

    @Bean
    public IntegrationFlow saveArticle(ArticleService articleService) {
        return IntegrationFlow
                .from("articleChannel")
                .handle(articleService, "save")
                .get();
    }

    @Bean
    public IntegrationFlow newsPaperFlow(NewsPaperService newsPaperService) {
        return IntegrationFlow
                .from("articlesChannel")
                .<List<Article>, List<Article>>transform(
                        (list) -> list.stream()
                                .filter((article -> article.text().size() > 30))
                                .collect(Collectors.toList())
                )
                .split()
                .<Article, Article>transform(article -> new Article(
                                article.id(),
                                article.title().toUpperCase(),
                                article.writer(),
                                article.text()
                        )
                )
                .aggregate()
                .handle(newsPaperService, "get")
                .<NewsPaper>filter((paper) -> {
                    log.info("filter");
                    return paper.articles().size() < 6;
                })
                .channel("newsPaperChannel")
                .get();
    }

    @Bean
    public IntegrationFlow saveNewsPaper(NewsPaperService newsPaperService) {
        return IntegrationFlow
                .from("newsPaperChannel")
                .handle(newsPaperService, "save")
                .get();
    }


}
