package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.dto.ProductDtoWeb;
import ru.otus.hw.mapper.ProductMapper;
import ru.otus.hw.model.Product;
import ru.otus.hw.repository.ProductRepository;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    private final ProductMapper productMapper;

    @Override
    public Flux<ProductDto> findAll() {
        return repository.findAll().map(productMapper::toProductDto);
    }

    @Override
    public Mono<ProductDto> findById(String id) {
        return repository.findById(id).map(productMapper::toProductDto);
    }

    @Override
    public Mono<ProductDtoWeb> save(Product order) {
        return repository.save(order).map(product -> productMapper.toProductDtoWeb(product, null));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
