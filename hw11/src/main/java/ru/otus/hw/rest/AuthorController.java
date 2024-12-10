package ru.otus.hw.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

@RestController
public class AuthorController {
    private final AuthorRepository repository;

    private final AuthorMapper mapper;

    public AuthorController(AuthorRepository repository, AuthorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping("/api/v1/authors")
    public Flux<AuthorDto> getAuthors() {
        return repository.findAll().map(mapper::toAuthorDto);
    }

}
