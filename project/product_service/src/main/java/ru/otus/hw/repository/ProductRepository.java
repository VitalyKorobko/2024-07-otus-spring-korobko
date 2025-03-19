package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.model.Product;

import java.util.Set;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findAllByIdIn(Set<String> ids);


}
