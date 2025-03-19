package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.repository.OrderRepository;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;

    private final OrderMapper orderMapper;

    @Override
    public Flux<OrderDto> findAll() {
        return repository.findAll().map(orderMapper::toOrderDto);
    }

    @Override
    public Flux<OrderDto> findByUserId(long userId) {
        return repository.findByUserId(userId).map(orderMapper::toOrderDto);
    }

    @Override
    public Mono<OrderDto> findById(String id) {
        return repository.findById(id).map(orderMapper::toOrderDto);
    }

    @Override
    public Mono<OrderDtoWeb> save(Order order) {
        return repository.save(order).map(o -> orderMapper.toOrderDtoWeb(o, null));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
