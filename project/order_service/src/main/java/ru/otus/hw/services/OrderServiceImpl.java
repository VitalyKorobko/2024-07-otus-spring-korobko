package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.repository.OrderRepository;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;

    @Override
    public Flux<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Order> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Order> save(Order order) {
        return repository.save(order);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
