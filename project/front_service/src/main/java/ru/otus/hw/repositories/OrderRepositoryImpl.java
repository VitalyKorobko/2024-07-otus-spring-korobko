package ru.otus.hw.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.exception.ImpossibleSaveEntityException;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final RestClient orderRestClient;

    private final TokenStorage tokenStorage;

    private final OrderMapper orderMapper;

    private final UserRepository userRepository;

    public OrderRepositoryImpl(@Qualifier("orderRestClient") RestClient orderRestClient,
                               TokenStorage tokenStorage, OrderMapper orderMapper, UserRepository userRepository) {
        this.orderRestClient = orderRestClient;
        this.tokenStorage = tokenStorage;
        this.orderMapper = orderMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public List<Order> findByUser(User user) {
        List<OrderDto> orderDtoList = orderRestClient.get()
                .uri("/api/v1/order/user/%s".formatted(user.getId()))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrderDto>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
        return orderDtoList != null ? orderDtoList.stream().
                map((orderDto -> orderMapper.toOrder(orderDto, user)))
                .toList() : null;
    }

    @Override
    @Retryable(retryFor = {Exception.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Optional<Order> findById(String id) {
        OrderDto orderDto = orderRestClient.get()
                .uri("/api/v1/order/%s".formatted(id))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve()
                .body(OrderDto.class);
        if (isNull(orderDto)) {
            throw new ImpossibleSaveEntityException("impossible to save order!");
        }
        User user = findUserById(orderDto.getUserId());
        return Optional.of(orderMapper.toOrder(orderDto, user));
    }

    @Override
    @Retryable(retryFor = {ImpossibleSaveEntityException.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Order create(Order order) {
        OrderDto orderDto = orderRestClient.post()
                .uri("/api/v1/order")
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .body(orderMapper.toOrderDto(order))
                .retrieve()
                .body(OrderDto.class);
        if (isNull(orderDto)) {
            throw new ImpossibleSaveEntityException("impossible to save order!");
        }
        User user = findUserById(orderDto.getUserId());
        return orderMapper.toOrder(orderDto, user);
    }

    @Override
    @Retryable(retryFor = {ImpossibleSaveEntityException.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Order update(Order order) {
        OrderDto orderDto = orderRestClient.patch()
                .uri("/api/v1/order/%s".formatted(order.getId()))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .body(orderMapper.toOrderDto(order))
                .retrieve()
                .body(OrderDto.class);
        if (isNull(orderDto)) {
            throw new ImpossibleSaveEntityException("impossible to save order!");
        }
        User user = findUserById(orderDto.getUserId());
        return orderMapper.toOrder(orderDto, user);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found"
                        .formatted(userId)));
    }
}
