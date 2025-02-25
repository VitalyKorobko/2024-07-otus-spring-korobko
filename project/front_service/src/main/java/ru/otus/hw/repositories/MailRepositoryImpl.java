package ru.otus.hw.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public OrderDtoForMail save(Order order) {
        System.out.println("\n===============================================\n");
        System.out.println(order);
        System.out.println(orderMapper.toOrderDtoForMail(order));
        System.out.println();
        OrderDtoForMail dto;
        try {
            dto = mailRestClient.post()
                    .uri("/api/v1/order")
                    .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                    .body(orderMapper.toOrderDtoForMail(order))
                    .retrieve()
                    .body(OrderDtoForMail.class);
        } catch (Exception e) {
            log.warn("email about order: %s wasn't sent, task was planed".formatted(order.toString()));
            throw new ImpossibleSaveEntityException("email about order: %s wasn't sent, task was planed"
                    .formatted(order.toString()) + e.getMessage());
        }
        return dto;


    }
}
