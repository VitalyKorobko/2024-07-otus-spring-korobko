package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.enums.Status;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.repository.OrderRepository;

import java.util.Date;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    @GetMapping("/api/v1/order")
    public Flux<OrderDto> getAll() {
        return orderRepository.findAll().map(orderMapper::toOrderDto);
    }


    @GetMapping("/api/v1/order/{id}")
    public Mono<OrderDto> get(@PathVariable("id") String id) {
        return orderRepository.findById(id).map(orderMapper::toOrderDto);
    }

    @PostMapping("/api/v1/order")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDtoWeb> create(@Valid @RequestBody Mono<OrderDto> monoOrderDto) {
        return monoOrderDto
                .flatMap(orderDto ->
                        orderRepository.save(
                                new Order(UUID.randomUUID().toString(),
                                        Status.valueOf(orderDto.getStatus().name().toString()),
                                        orderDto.getStartDate(),
                                        orderDto.getEndDate(),
                                        orderDto.getOrderField(),
                                        orderDto.getUserId())))
                .map(order -> orderMapper.toOrderDtoWeb(order, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new OrderDtoWeb(
                                null,
                                Status.valueOf(ex.getFieldValue("status").toString()),
                                (Date) ex.getFieldValue("startDate"),
                                (Date) ex.getFieldValue("endDate"),
                                ex.getFieldValue("orderField").toString(),
                                Integer.parseInt(ex.getFieldValue("userId").toString()),
                                ex.getFieldError().getDefaultMessage()))
                );
    }

    @PatchMapping("/api/v1/order/{id}")
    public Mono<OrderDtoWeb> update(@Valid @RequestBody Mono<OrderDto> monoOrderDto,
                                    @PathVariable("id") String id) {
        return monoOrderDto
                .zipWhen(orderDto -> orderRepository.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("order with id = %s not found"
                                .formatted(id)))))
                .flatMap(tuple ->
                        orderRepository.save(
                                new Order(id,
                                        tuple.getT1().getStatus(),
                                        tuple.getT1().getStartDate(),
                                        tuple.getT1().getEndDate(),
                                        tuple.getT1().getOrderField(),
                                        tuple.getT1().getUserId())))
                .map(order -> orderMapper.toOrderDtoWeb(order, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new OrderDtoWeb(
                                id,
                                Status.valueOf(ex.getFieldValue("status").toString()),
                                (Date) ex.getFieldValue("startDate"),
                                (Date) ex.getFieldValue("endDate"),
                                ex.getFieldValue("orderField").toString(),
                                Integer.parseInt(ex.getFieldValue("userId").toString()),
                                ex.getFieldError().getDefaultMessage()))
                );
    }

    @DeleteMapping("/api/v1/order/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return orderRepository.deleteById(id);
    }


}
