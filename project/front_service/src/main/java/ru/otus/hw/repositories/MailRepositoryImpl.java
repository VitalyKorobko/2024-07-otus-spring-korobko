package ru.otus.hw.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.dto.OrderDtoForMail;
import ru.otus.hw.exception.ImpossibleSaveEntityException;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.models.Order;

@Slf4j
@Component
public class MailRepositoryImpl implements MailRepository {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final RestClient mailRestClient;

    private final TokenStorage tokenStorage;

    private final OrderMapper orderMapper;

    public MailRepositoryImpl(@Qualifier("mailRestClient") RestClient mailRestClient,
                              TokenStorage tokenStorage, OrderMapper orderMapper) {
        this.mailRestClient = mailRestClient;
        this.tokenStorage = tokenStorage;
        this.orderMapper = orderMapper;
    }

    @Retryable(retryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public OrderDtoForMail save(Order order) {
        OrderDtoForMail dto;
        try {
            dto = mailRestClient.post()
                    .uri("/api/v1/order")
                    .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                    .body(orderMapper.toOrderDtoForMail(order))
                    .retrieve()
                    .body(OrderDtoForMail.class);
        } catch (Exception e) {
            log.warn("email about order: {} wasn't sent, task was planed", order.toString());
            throw new ImpossibleSaveEntityException("email about order: %s wasn't sent, task was planed"
                    .formatted(order.toString()) + e.getMessage());
        }
        log.info("email about order: {} was sent", order);
        return dto;


    }
}
