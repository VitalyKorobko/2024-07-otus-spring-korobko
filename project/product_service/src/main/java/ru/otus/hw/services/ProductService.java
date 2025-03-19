package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.dto.ProductDtoWeb;
import ru.otus.hw.model.Product;

public interface ProductService {
    Flux<ProductDto> findAll();

    Mono<ProductDto> findById(String id);

    Mono<ProductDtoWeb> save(Product order);

    Mono<Void> deleteById(String id);



}
