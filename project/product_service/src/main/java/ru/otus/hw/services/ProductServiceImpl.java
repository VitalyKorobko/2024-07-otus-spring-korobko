package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.model.Product;
import ru.otus.hw.repository.ProductRepository;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public Flux<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Product> save(Product order) {
        return repository.save(order);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
