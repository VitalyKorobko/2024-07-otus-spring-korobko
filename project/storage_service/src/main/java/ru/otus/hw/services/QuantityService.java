package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.model.Quantity;

public interface QuantityService {
    Flux<Quantity> findAll();

    Mono<Quantity> findById(String id);

    Mono<Quantity> save(Quantity order);

    Mono<Void> deleteById(String id);

    Mono<Quantity> findByProductId(String id);



}
