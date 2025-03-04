package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.repository.QuantityRepository;

@RequiredArgsConstructor
@Service
public class QuantityServiceImpl implements QuantityService {
    private final QuantityRepository repository;

    @Override
    public Flux<Quantity> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Quantity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Quantity> save(Quantity order) {
        return repository.save(order);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Quantity> findByProductId(String id) {
        return repository.findByProductId(id);
    }

}
