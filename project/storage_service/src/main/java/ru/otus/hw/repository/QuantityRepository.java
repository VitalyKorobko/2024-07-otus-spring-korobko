package ru.otus.hw.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.otus.hw.model.Quantity;

public interface QuantityRepository extends ReactiveMongoRepository<Quantity, String> {
    Mono<Quantity> findByProductId(String productId);


}
