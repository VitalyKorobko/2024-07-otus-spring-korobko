package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;

public interface OrderService {
    Flux<Order> findAll();

    Mono<Order> findById(String id);

    Mono<Order> save(Order order);

    Mono<Void> deleteById(String id);



}
