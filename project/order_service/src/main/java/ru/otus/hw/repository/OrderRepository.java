package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.hw.domain.Order;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {


}
