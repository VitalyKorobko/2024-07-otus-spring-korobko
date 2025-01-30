package ru.otus.hw.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Slf4j
@RestController
public class AuthorController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @CircuitBreaker(name = "getAllAuthorsCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToGetAllAuthors")
    @GetMapping("/api/v1/authors")
    public List<AuthorDto> getAuthors() {
        return authorService.findAll();
    }

    private List<AuthorDto> circuitBreakerFallBackToGetAllAuthors(Throwable e) {
        log.error("circuit breaker got open state when receiving author list. Err: {}: {}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }
}

