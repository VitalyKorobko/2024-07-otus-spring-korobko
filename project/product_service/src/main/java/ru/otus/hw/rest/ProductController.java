package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.model.Product;
import ru.otus.hw.services.ProductService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final ProductService productService;

    @GetMapping("/api/v1/product")
    @CircuitBreaker(name = "getAllProductCircuitBreaker", fallbackMethod = "circuitBreakerFallBackAllProduct")
    public Flux<ProductDto> getAll() {
        return productService.findAll();
    }

    @GetMapping("/api/v1/product/{id}")
    @CircuitBreaker(name = "getProductCircuitBreaker", fallbackMethod = "circuitBreakerFallBackProduct")
    public Mono<ProductDto> get(@PathVariable("id") String id) {
        return productService.findById(id);
    }

    @PostMapping("/api/v1/product")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "createProductCircuitBreaker", fallbackMethod = "circuitBreakerFallBackCreateProduct")
    public Mono<ProductDtoWeb> create(@Valid @RequestBody Mono<ProductDto> monoProductDto) {
        return monoProductDto
                .flatMap(productDto ->
                        productService.save(
                                new Product(UUID.randomUUID().toString(),
                                        productDto.getTitle(),
                                        productDto.getRef(),
                                        productDto.getImage(),
                                        productDto.getDescription(),
                                        productDto.getPrice(),
                                        productDto.getSellerId())))
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
    @CircuitBreaker(name = "updateProductCircuitBreaker", fallbackMethod = "circuitBreakerFallBackUpdateProduct")
    public Mono<ProductDtoWeb> update(@Valid @RequestBody Mono<ProductDto> monoProductDto,
                                      @PathVariable("id") String id) {
        log.info("update product: %s".formatted(id));
        return monoProductDto
                .zipWhen(productDto -> productService.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("product with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple -> productService.save(
                        new Product(id,
                                tuple.getT1().getTitle(),
                                tuple.getT1().getRef(),
                                tuple.getT1().getImage(),
                                tuple.getT1().getDescription(),
                                tuple.getT1().getPrice(),
                                tuple.getT1().getSellerId())))
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
        return productService.deleteById(id);
    }

    private Flux<ProductDto> circuitBreakerFallBackAllProduct(Throwable e) {
        log.error("circuit breaker got open state when get all product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<ProductDto> circuitBreakerFallBackProduct(String id, Throwable e) {
        log.error("circuit breaker got open state when get product with id={}: Err: {}:{}", id, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<ProductDtoWeb> circuitBreakerFallBackCreateProduct(Mono<ProductDto> monoProductDto, Throwable e) {
        log.error("circuit breaker got open state when create product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<ProductDtoWeb> circuitBreakerFallBackUpdateProduct(Mono<ProductDto> monoProductDto, Throwable e) {
        log.error("circuit breaker got open state when update product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }




}
