package ru.otus.hw.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;


@RestController
public class GenreController {
    private final GenreRepository repository;

    private final GenreMapper mapper;

    public GenreController(GenreRepository repository, GenreMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping("/api/v1/genres")
    public Flux<GenreDto> getGenres() {
        return repository.findAll().map(mapper::toGenreDto);
    }

}
