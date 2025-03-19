package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.QuantityDto;
import ru.otus.hw.dto.QuantityDtoWeb;
import ru.otus.hw.model.Quantity;

public interface QuantityService {
    Flux<QuantityDto> findAll();

    Mono<Quantity> findById(String id);

    Mono<QuantityDtoWeb> save(Quantity order);

    Mono<Void> deleteById(String id);

    Mono<QuantityDto> findByProductId(String id);



}
