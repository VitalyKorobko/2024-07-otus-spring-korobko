package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.exception.NotAvailableException;


@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final TokenStorage tokenStorage;

    @CircuitBreaker(name = "tokenSendCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
    @PostMapping(value = "/api/v1/token")
    public void saveToken(@RequestBody String token) {
        tokenStorage.setToken(token);
        log.info("save token");
    }

    private void circuitBreakerFallBack(String token, Throwable e) {
        log.error("circuit breaker got open state when get token: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }
}
