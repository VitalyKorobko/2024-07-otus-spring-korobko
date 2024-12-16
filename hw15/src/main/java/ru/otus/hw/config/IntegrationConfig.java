package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.QueueChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PublishSubscribeChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;
import ru.otus.hw.services.ArticleService;
import ru.otus.hw.services.NewsPaperService;
import ru.otus.hw.services.WriterService;


@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public QueueChannelSpec writerChannel() {
        return MessageChannels.queue(5);
    }

    @Bean
    public PublishSubscribeChannelSpec<?> allowedArticlesChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public PublishSubscribeChannelSpec<?> badArticlesChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public QueueChannelSpec articlesChannel() {
        return MessageChannels.queue(15);
    }

    @Bean
    public PublishSubscribeChannelSpec<?> newsPaperChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public IntegrationFlow articleFlow(ArticleService articleService, WriterService writerService) {
        return IntegrationFlow
                .from("writerChannel")
                .handle(writerService, "create")
                .handle(articleService, "create")
                .<Article, Boolean>route(article -> article.text().size() > 30,
                        mapping -> mapping
                                .subFlowMapping(true, sf -> sf.channel("allowedArticlesChannel"))
                                .subFlowMapping(false, sf -> sf.channel("badArticlesChannel"))
                )
                .get();
    }

    @Bean
    public IntegrationFlow saveArticle(ArticleService articleService) {
        return IntegrationFlow
                .from("allowedArticlesChannel")
                .handle(articleService, "save")
                .get();
    }

    @Bean
    public IntegrationFlow badArticlesFlow() {
        return IntegrationFlow
                .from("badArticlesChannel")
                .log()
                .get();
    }

    @Bean
    public IntegrationFlow newsPaperFlow(NewsPaperService newsPaperService) {
        return IntegrationFlow
                .from("articlesChannel")
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
                    log.info("filter by size = :{} ", paper.articles().size());
                    return paper.articles().size() > 6;
                })
                .<NewsPaper, NewsPaper>transform(paper -> new NewsPaper(paper.id(),
                        paper.name().toUpperCase(),
                        paper.articles()))
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
