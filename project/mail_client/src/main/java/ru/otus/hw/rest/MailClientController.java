package ru.otus.hw.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.config.OrderValueStorage;
import ru.otus.hw.config.ReactiveSender;
import ru.otus.hw.model.Order;
import ru.otus.hw.model.OrderValue;
import ru.otus.hw.model.Request;
import ru.otus.hw.model.RequestId;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
public class MailClientController {
    private final AtomicLong idGenerator = new AtomicLong(0);

    private final ReactiveSender<Order, Request> requestSender;

    private final OrderValueStorage orderValueStorage;

    public MailClientController(ReactiveSender<Order, Request> requestSender,
                                OrderValueStorage orderValueStorage) {
        this.requestSender = requestSender;
        this.orderValueStorage = orderValueStorage;
    }

    @PostMapping(value = "/api/v1/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OrderValue> getResultAboutMailSending(@RequestBody Order order) {
        log.info("sending info about the Order to Customer, order: {}", order);
        var request = new Request(new RequestId(idGenerator.incrementAndGet()), order);

        return requestSender.send(request, requestSend -> log.info("send ok: {}", requestSend))
                .flatMap(senderResult -> orderValueStorage
                        .get(new RequestId(senderResult.correlationMetadata().id())))
                .doOnNext(orderValue -> log.info("result of sending:{}", orderValue));
    }


}
