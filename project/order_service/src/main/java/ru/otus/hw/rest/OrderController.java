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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.enums.Status;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.exceptions.ParseDateException;
import ru.otus.hw.services.OrderService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;



@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final OrderService orderService;

    @GetMapping("/api/v1/order")
    @CircuitBreaker(name = "getAllOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBackAllOrder")
    public Flux<OrderDto> getAll() {
        return orderService.findAll();
    }

    @GetMapping("/api/v1/order/{id}")
    @CircuitBreaker(name = "getOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBackOrder")
    public Mono<OrderDto> get(@PathVariable("id") String id) {
        return orderService.findById(id);
    }

    @PostMapping("/api/v1/order")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "createOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBackCreateOrder")
    public Mono<OrderDtoWeb> create(@Valid @RequestBody Mono<OrderDto> monoOrderDto) {
        return monoOrderDto
                .flatMap(orderDto ->
                        orderService.save(
                                new Order(UUID.randomUUID().toString(),
                                        Status.valueOf(orderDto.getStatus()),
                                        LocalDateTime.ofEpochSecond(orderDto.getStartDate(), 0, ZoneOffset.UTC),
                                        LocalDateTime.ofEpochSecond(orderDto.getEndDate(), 0, ZoneOffset.UTC),
                                        orderDto.getOrderField(),
                                        orderDto.getUserId())))
                .onErrorResume(WebExchangeBindException.class, ex ->
                                Mono.just(new OrderDtoWeb(
                                        null,
                                        getStatus(ex.getFieldValue("status").toString()),
                                        getDate(ex.getFieldValue("startDate").toString()),
                                        getDate(ex.getFieldValue("endDate").toString()),
                                        ex.getFieldValue("orderField").toString(),
                                        Integer.parseInt(ex.getFieldValue("userId").toString()),
                                        ex.getFieldError().getDefaultMessage()))
                );
    }

    @PatchMapping("/api/v1/order/{id}")
    @CircuitBreaker(name = "updateOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBackUpdateOrder")
    public Mono<OrderDtoWeb> update(@Valid @RequestBody Mono<OrderDto> monoOrderDto,
                                    @PathVariable("id") String id) {
        return monoOrderDto
                .zipWhen(orderDto -> orderService.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("order with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple ->
                        orderService.save(
                                new Order(id,
                                        Status.valueOf(tuple.getT1().getStatus()),
                                        LocalDateTime.ofEpochSecond(tuple.getT1().getStartDate(), 0, ZoneOffset.UTC),
                                        LocalDateTime.ofEpochSecond(tuple.getT1().getEndDate(), 0, ZoneOffset.UTC),
                                        tuple.getT1().getOrderField(),
                                        tuple.getT1().getUserId())))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new OrderDtoWeb(
                                id,
                                getStatus(ex.getFieldValue("status").toString()),
                                getDate(ex.getFieldValue("startDate").toString()),
                                getDate(ex.getFieldValue("endDate").toString()),
                                ex.getFieldValue("orderField").toString(),
                                Integer.parseInt(ex.getFieldValue("userId").toString()),
                                ex.getFieldError().getDefaultMessage()))
                );
    }


    @DeleteMapping("/api/v1/order/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return orderService.deleteById(id);
    }

    private Status getStatus(String status) {
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException ex) {
            log.warn("can`t covert status %s".formatted(status));
            return null;
        }
        return Status.valueOf(status);
    }

    private long getDate(String epochTime) {
        try {
            Long.parseLong(epochTime);
        } catch (NumberFormatException e) {
            throw new ParseDateException("Неверный формат даты: ");
        }
        return  Long.parseLong(epochTime);
    }

    private Flux<OrderDto> circuitBreakerFallBackAllOrder(Throwable e) {
        log.error("circuit breaker got open state when get all order: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<OrderDto> circuitBreakerFallBackOrder(String id, Throwable e) {
        log.error("circuit breaker got open state when get order with id={}: Err: {}:{}", id, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<OrderDtoWeb> circuitBreakerFallBackCreateOrder(Mono<OrderDto> monoOrderDto, Throwable e) {
        log.error("circuit breaker got open state when create order: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private Mono<OrderDtoWeb> circuitBreakerFallBackUpdateOrder(Mono<OrderDto> monoOrderDto, Throwable e) {
        log.error("circuit breaker got open state when update order: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }


}
