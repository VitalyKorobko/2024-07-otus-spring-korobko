package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.services.TokenStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final TokenStorageService service;

    private final ReactiveJwtDecoder jwtDecoder;

    @CircuitBreaker(name = "tokenSendCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
    @PostMapping("/api/v1/token")
    public void saveToken(@RequestBody String token) {
        var jwtMono = jwtDecoder.decode(token);
        jwtMono.doOnNext(jwt -> {
                    service.add(jwt.getSubject(), jwt.getTokenValue());
                    log.info("save token");
                    log.info("token list: {}", service.findAll());
                })
                .doOnError(JwtValidationException.class, (e) -> log.warn("not saved: {}", e.getMessage()))
                .doOnError(BadJwtException.class, (e) -> log.warn("it isn't token: {}", e.getMessage()))
                .subscribe();
    }

    @CircuitBreaker(name = "tokensSendCircuitBreaker", fallbackMethod = "circuitBreakerTokensFallBack")
    @PostMapping("/api/v1/tokens")
    public void saveTokens(@RequestBody List<String> tokens) {
        for (String token : tokens) {
            var jwtMono = jwtDecoder.decode(token);
            jwtMono.doOnNext(jwt -> {
                        service.add(jwt.getSubject(), token);
                        log.info("save tokens");
                        log.info("token list: {}", service.findAll());
                    })
                    .doOnError(JwtValidationException.class,
                            (e) -> log.warn("not saved: {}", e.getMessage()))
                    .doOnError(BadJwtException.class,
                            (e) -> log.warn("it isn't token: {}", e.getMessage()))
                    .subscribe();
        }
    }

    private void circuitBreakerFallBack(String token, Throwable e) {
        log.error("circuit breaker got open state when get token: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private void circuitBreakerTokensFallBack(List<String> tokens, Throwable e) {
        log.error("circuit breaker got open state when get tokens: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }
}