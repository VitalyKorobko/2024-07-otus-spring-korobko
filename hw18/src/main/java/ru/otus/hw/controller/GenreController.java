package ru.otus.hw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Slf4j
@RestController
public class GenreController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final GenreService genreService;

    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    public GenreController(GenreService genreService, Resilience4JCircuitBreakerFactory circuitBreakerFactory) {
        this.genreService = genreService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/api/v1/genres")
    public List<GenreDto> getGenres() {
        return circuitBreakerFactory.create("getAllGenresCircuitBreaker").run(
                genreService::findAll,
                this::circuitBreakerFallBackToGetAllGenres
        );
    }

    private List<GenreDto> circuitBreakerFallBackToGetAllGenres(Throwable e) {
        log.error("circuit breaker got open state when receiving genre list. Err: {}: {}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

}
