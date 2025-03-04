package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;

public interface OrderService {
    Flux<OrderDto> findAll();

    Mono<OrderDto> findById(String id);

    Mono<OrderDtoWeb> save(Order order);

    Mono<Void> deleteById(String id);



}
