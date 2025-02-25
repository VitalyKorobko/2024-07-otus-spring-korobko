package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.dto.ProductDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.ProductMapper;
import ru.otus.hw.model.Product;
import ru.otus.hw.repository.ProductRepository;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @GetMapping("/api/v1/product")
    public Flux<ProductDto> getAll() {
        return productRepository.findAll().map(productMapper::toProductDto);
    }

    @GetMapping("/api/v1/product/{id}")
    public Mono<ProductDto> get(@PathVariable("id") String id) {
        return productRepository.findById(id).map(productMapper::toProductDto);
    }

    @PostMapping("/api/v1/product")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDtoWeb> create(@Valid @RequestBody Mono<ProductDto> monoProductDto) {
        return monoProductDto
                .flatMap(productDto ->
                        productRepository.save(
                                new Product(UUID.randomUUID().toString(),
                                        productDto.getTitle(),
                                        productDto.getRef(),
                                        productDto.getImage(),
                                        productDto.getDescription(),
                                        productDto.getPrice(),
                                        productDto.getSellerId())))
                .map(product -> productMapper.toProductDtoWeb(product, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new ProductDtoWeb(
                                null,
                                ex.getFieldValue("title").toString(),
                                ex.getFieldValue("ref").toString(),
                                ex.getFieldValue("image").toString(),
                                ex.getFieldValue("description").toString(),
                                Integer.parseInt(ex.getFieldValue("price").toString()),
                                Long.parseLong(ex.getFieldValue("sellerId").toString()),
                                ex.getFieldError().getDefaultMessage()))
                );
    }

    @PatchMapping("/api/v1/product/{id}")
    public Mono<ProductDtoWeb> update(@Valid @RequestBody Mono<ProductDto> monoProductDto,
                                      @PathVariable("id") String id) {
        log.info("update product: %s".formatted(id));
        return monoProductDto
                .zipWhen(productDto -> productRepository.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("product with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple -> productRepository.save(
                        new Product(id,
                                tuple.getT1().getTitle(),
                                tuple.getT1().getRef(),
                                tuple.getT1().getImage(),
                                tuple.getT1().getDescription(),
                                tuple.getT1().getPrice(),
                                tuple.getT1().getSellerId())))
                .map(product -> productMapper.toProductDtoWeb(product, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new ProductDtoWeb(id,
                                ex.getFieldValue("title").toString(),
                                ex.getFieldValue("ref").toString(),
                                ex.getFieldValue("image").toString(),
                                ex.getFieldValue("description").toString(),
                                Integer.parseInt(ex.getFieldValue("price").toString()),
                                Long.parseLong(ex.getFieldValue("sellerId").toString()),
                                ex.getFieldError().getDefaultMessage())));
    }

    @DeleteMapping("/api/v1/product/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return productRepository.deleteById(id);
    }




}
