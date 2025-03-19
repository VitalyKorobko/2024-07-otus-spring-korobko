package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.QuantityDto;
import ru.otus.hw.dto.QuantityDtoWeb;
import ru.otus.hw.mapper.QuantityMapper;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.repository.QuantityRepository;

@RequiredArgsConstructor
@Service
public class QuantityServiceImpl implements QuantityService {
    private final QuantityRepository repository;

    private final QuantityMapper mapper;

    @Override
    public Flux<QuantityDto> findAll() {
        return repository.findAll().map(mapper::toQuantityDto);
    }

    @Override
    public Mono<Quantity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<QuantityDtoWeb> save(Quantity order) {
        return repository.save(order).map(quantity -> mapper.toQuantityDtoWeb(quantity, null));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<QuantityDto> findByProductId(String id) {
        return repository.findByProductId(id).map(mapper::toQuantityDto);
    }

}
