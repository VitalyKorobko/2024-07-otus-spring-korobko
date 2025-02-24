package ru.otus.hw.rest;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.model.Order;
import ru.otus.hw.sender.MailSender;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class
SourceDataController {
    private static final Logger log = LoggerFactory.getLogger(SourceDataController.class);

    private final MailSender<Order> mailSender;

    private final Executor blockingExecutor;


    public SourceDataController(MailSender<Order> mailSender,
                                @Qualifier("blockingExecutor") Executor blockingExecutor) {
        this.mailSender = mailSender;
        this.blockingExecutor = blockingExecutor;
    }

    @PostMapping(value = "/api/v1/order")
    public Mono<Order> dataMono(@RequestBody Order order) {
        log.info("request for sending order start, order:{}", order);
        var future = CompletableFuture
                .supplyAsync(() -> mailSender.send(order), blockingExecutor);
        var mono  = Mono.fromFuture(future);
        log.info("Method request for sending order was done");
        return mono;
    }

}
