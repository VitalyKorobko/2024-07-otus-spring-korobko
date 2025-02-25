package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.domain.Order;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.enums.Status;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.ParseDateException;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;



@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ObjectMapper objectMapper;

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
                                        Status.valueOf(orderDto.getStatus()),
                                        LocalDateTime.ofEpochSecond(orderDto.getStartDate(), 0, ZoneOffset.UTC),
                                        LocalDateTime.ofEpochSecond(orderDto.getEndDate(), 0, ZoneOffset.UTC),
                                        orderDto.getOrderField(),
                                        orderDto.getUserId())))
                .map(order -> orderMapper.toOrderDtoWeb(order, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                                Mono.just(new OrderDtoWeb(
                                        null,
                                        getStatus(ex.getFieldValue("status").toString()),
                                        getDate(ex.getFieldValue("startDate").toString()),
                                        getDate(ex.getFieldValue("endDate").toString()),
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
                                        Status.valueOf(tuple.getT1().getStatus()),
                                        LocalDateTime.ofEpochSecond(tuple.getT1().getStartDate(), 0, ZoneOffset.UTC),
                                        LocalDateTime.ofEpochSecond(tuple.getT1().getEndDate(), 0, ZoneOffset.UTC),
                                        tuple.getT1().getOrderField(),
                                        tuple.getT1().getUserId())))
                .map(order -> orderMapper.toOrderDtoWeb(order, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new OrderDtoWeb(
                                id,
                                getStatus(ex.getFieldValue("status").toString()),
                                getDate(ex.getFieldValue("startDate").toString()),
                                getDate(ex.getFieldValue("endDate").toString()),
                                ex.getFieldValue("orderField").toString(),
                                Integer.parseInt(ex.getFieldValue("userId").toString()),
                                ex.getFieldError().getDefaultMessage()))
                );
    }


    @DeleteMapping("/api/v1/order/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return orderRepository.deleteById(id);
    }

    private Status getStatus(String status) {
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException ex) {
            log.warn("can`t covert status %s".formatted(status));
            return null;
        }
        return Status.valueOf(status);
    }

    private long getDate(String epochTime) {
        try {
            Long.parseLong(epochTime);
        } catch (NumberFormatException e) {
            throw new ParseDateException("Неверный формат даты: ");
        }
        return  Long.parseLong(epochTime);
    }


}
