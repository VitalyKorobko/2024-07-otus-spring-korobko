package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.otus.hw.mapper.QuantityMapper;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.repository.QuantityRepository;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class QuantityController {
    private final QuantityRepository repository;

    private final QuantityMapper mapper;

    @GetMapping("/api/v1/quantity")
    public Flux<QuantityDto> getAll() {
        return repository.findAll().map(mapper::toQuantityDto);
    }

    @GetMapping("/api/v1/quantity/{id}")
    public Mono<QuantityDto> get(@PathVariable("id") String id) {
        return repository.findByProductId(id).map(mapper::toQuantityDto);
    }

    @PostMapping("/api/v1/quantity")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<QuantityDtoWeb> create(@Valid @RequestBody Mono<QuantityDto> monoQuantityDto) {
        return monoQuantityDto
                .flatMap(quantityDto ->
                        repository.save(
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
    public Mono<QuantityDtoWeb> update(@Valid @RequestBody Mono<QuantityDto> monoQuantityDto,
                                       @PathVariable("id") String id) {
        return monoQuantityDto
                .zipWhen(quantityDto -> repository.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("quantity with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple ->
                        repository.save(
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
        return repository.deleteById(id);
    }


}
