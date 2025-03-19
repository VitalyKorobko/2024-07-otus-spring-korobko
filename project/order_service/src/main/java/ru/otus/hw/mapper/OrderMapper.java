package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.domain.Order;

import java.time.ZoneOffset;


@Component
public class OrderMapper {

    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                order.getStartDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getEndDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getOrderField(),
                order.getUserId()
        );
    }

    public OrderDtoWeb toOrderDtoWeb(Order order, String message) {
        return new OrderDtoWeb(
                order.getId(),
                order.getStatus(),
                order.getStartDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getEndDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getOrderField(),
                order.getUserId(),
                message
        );
    }
}
