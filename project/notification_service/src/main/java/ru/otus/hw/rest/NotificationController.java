package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.exception.NotAvailableException;
import ru.otus.hw.model.Order;
import ru.otus.hw.sender.MailSender;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class
NotificationController {

    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final MailSender<Order> mailSender;

    private final Executor blockingExecutor;


    public NotificationController(MailSender<Order> mailSender,
                                  @Qualifier("blockingExecutor") Executor blockingExecutor) {
        this.mailSender = mailSender;
        this.blockingExecutor = blockingExecutor;
    }

    @PostMapping(value = "/api/v1/order")
    @CircuitBreaker(name = "getOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
    public Mono<Order> dataMono(@RequestBody Order order) {
        log.info("request for sending order start, order:{}", order);
        var future = CompletableFuture
                .supplyAsync(() -> mailSender.send(order), blockingExecutor);
        var mono  = Mono.fromFuture(future);
        log.info("Method request for sending order was done");
        return mono;
    }

    private Mono<Order> circuitBreakerFallBack(Order order, Throwable e) {
        log.error("circuit breaker got open state when get order: {} Err: {}:{}", order, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

}
