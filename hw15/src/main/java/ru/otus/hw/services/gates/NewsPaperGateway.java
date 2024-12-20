package ru.otus.hw.services.gates;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;

import java.util.List;

@MessagingGateway
public interface NewsPaperGateway {

    @Gateway(requestChannel = "articlesChannel", replyChannel = "newsPaperChannel")
    NewsPaper create(List<Article> articles);

}
