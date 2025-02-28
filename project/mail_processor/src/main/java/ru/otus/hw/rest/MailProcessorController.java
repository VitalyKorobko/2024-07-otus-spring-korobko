package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserters;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.exception.NotAvailableException;
import ru.otus.hw.model.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailProcessorController {

    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final WebClient webClient;

    private final TokenStorage tokenStorage;

    @GetMapping(value = "/api/v1/order")
    @CircuitBreaker(name = "getOrderCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
    public Mono<Order> getOrder(@RequestBody Order order) {
        log.info("request for data, order:{}", order);
        return webClient.post().uri(String.format("/api/v1/order"))
                .body(BodyInserters.fromValue(order))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnNext(oder -> log.info("processor return order: {}", order));
    }

    private Mono<Order> circuitBreakerFallBack(Order order, Throwable e) {
        log.error("circuit breaker got open state when get order: {} Err: {}:{}", order, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }


}
