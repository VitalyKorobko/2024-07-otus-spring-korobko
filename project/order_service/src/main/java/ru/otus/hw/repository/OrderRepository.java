package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.domain.Order;

import java.util.Set;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

//    Flux<Order> findAllByIdIn(Set<String> ids);




}
