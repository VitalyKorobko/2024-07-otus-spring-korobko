package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.model.Product;

public interface ProductService {
    Flux<Product> findAll();

    Mono<Product> findById(String id);

    Mono<Product> save(Product order);

    Mono<Void> deleteById(String id);



}
