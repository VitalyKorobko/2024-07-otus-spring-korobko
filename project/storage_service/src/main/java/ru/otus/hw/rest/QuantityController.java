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
import ru.otus.hw.dto.QuantityDto;
import ru.otus.hw.dto.QuantityDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.mapper.QuantityMapper;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.services.QuantityService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Slf4j
public class QuantityController {

    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final QuantityService service;

    private final QuantityMapper mapper;

    @GetMapping("/api/v1/quantity")
    @CircuitBreaker(name = "getAllQuantityCircuitBreaker", fallbackMethod = "circuitBreakerFallBackAllQuantity")
    public Flux<QuantityDto> getAll() {
        return service.findAll().map(mapper::toQuantityDto);
    }

    @GetMapping("/api/v1/quantity/{id}")
    @CircuitBreaker(name = "getQuantityCircuitBreaker", fallbackMethod = "circuitBreakerFallBackQuantity")
    public Mono<QuantityDto> get(@PathVariable("id") String id) {
        return service.findByProductId(id).map(mapper::toQuantityDto);
    }

    @PostMapping("/api/v1/quantity")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "createQuantityCircuitBreaker", fallbackMethod = "circuitBreakerFallBackCreateQuantity")
    public Mono<QuantityDtoWeb> create(@Valid @RequestBody Mono<QuantityDto> monoQuantityDto) {
        return monoQuantityDto
                .flatMap(quantityDto ->
                        service.save(
                                new Quantity(UUID.randomUUID().toString(),
                                        quantityDto.getProductCount(),
                                        quantityDto.getProductId())
                        )
                )
                .map(quantity -> mapper.toQuantityDtoWeb(quantity, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new QuantityDtoWeb(
                                null,
                                Integer.parseInt(ex.getFieldValue("productCount").toString()),
                                ex.getFieldValue("productId").toString(),
                                ex.getFieldError().getDefaultMessage())
                        )
                );
    }

    @PatchMapping("/api/v1/quantity/{id}")
    @CircuitBreaker(name = "updateQuantityCircuitBreaker", fallbackMethod = "circuitBreakerFallBackUpdateQuantity")
    public Mono<QuantityDtoWeb> update(@Valid @RequestBody Mono<QuantityDto> monoQuantityDto,
                                       @PathVariable("id") String id) {
        return monoQuantityDto
                .zipWhen(quantityDto -> service.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("quantity with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple ->
                        service.save(
                                new Quantity(id,
                                        tuple.getT1().getProductCount(),
                                        tuple.getT1().getProductId())))
                .map(quantity -> mapper.toQuantityDtoWeb(quantity, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new QuantityDtoWeb(
                                id,
                                Integer.parseInt(ex.getFieldValue("productCount").toString()),
                                ex.getFieldValue("productId").toString(),
                                ex.getFieldError().getDefaultMessage())
                        )
                );
    }

    @DeleteMapping("/api/v1/quantity/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.deleteById(id);
    }

    private Flux<QuantityDto> circuitBreakerFallBackAllQuantity(Throwable e) {
        log.error("circuit breaker got open state when get all product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<QuantityDto> circuitBreakerFallBackQuantity(String id, Throwable e) {
        log.error("circuit breaker got open state when get product with id={}: Err: {}:{}", id, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<QuantityDtoWeb> circuitBreakerFallBackCreateQuantity(Mono<QuantityDto> monoQuantityDto, Throwable e) {
        log.error("circuit breaker got open state when create product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<QuantityDtoWeb> circuitBreakerFallBackUpdateQuantity(Mono<QuantityDto> monoQuantityDto, Throwable e) {
        log.error("circuit breaker got open state when update product: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }


}
