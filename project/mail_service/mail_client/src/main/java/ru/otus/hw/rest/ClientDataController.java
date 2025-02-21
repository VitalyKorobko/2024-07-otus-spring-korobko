package ru.otus.hw.rest;


import org.springframework.web.bind.annotation.*;
import ru.otus.hw.config.OrderValueStorage;
import ru.otus.hw.config.ReactiveSender;
import ru.otus.hw.model.Order;
import ru.otus.hw.model.OrderValue;
import ru.otus.hw.model.Request;
import ru.otus.hw.model.RequestId;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


//  http://localhost:8082/data/5
@RestController
public class ClientDataController {
    private static final Logger log = LoggerFactory.getLogger(ClientDataController.class);

    private final AtomicLong idGenerator = new AtomicLong(0);

//    private final WebClient webClient;

    private final ReactiveSender<Order, Request> requestSender;

    private final OrderValueStorage orderValueStorage;

    public ClientDataController(ReactiveSender<Order, Request> requestSender, OrderValueStorage orderValueStorage) {
//        this.webClient = webClient;
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
