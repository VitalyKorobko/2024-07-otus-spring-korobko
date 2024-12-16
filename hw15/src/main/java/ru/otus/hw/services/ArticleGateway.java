package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Article;


@MessagingGateway
public interface ArticleGateway {

    @Gateway(requestChannel = "writerChannel", replyChannel = "articleChannel")
    Article create(String writerName);

}
