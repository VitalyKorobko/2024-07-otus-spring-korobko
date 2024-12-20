package ru.otus.hw.services.gates;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Article;


@MessagingGateway
public interface ArticleGateway {

    @Gateway(requestChannel = "writerChannel", replyChannel = "allowedArticlesChannel")
    Article create(String writerName);

}
