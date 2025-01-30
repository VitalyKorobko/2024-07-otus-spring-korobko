package ru.otus.hw.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
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

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @CircuitBreaker(name = "getAllGenresCircuitBreaker", fallbackMethod = "circuitBreakerFallBackToGetAllGenres")
    @GetMapping("/api/v1/genres")
    public List<GenreDto> getGenres() {
        return genreService.findAll();
    }

    private List<GenreDto> circuitBreakerFallBackToGetAllGenres(Throwable e) {
        log.error("circuit breaker got open state when receiving genre list. Err: {}: {}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

}
