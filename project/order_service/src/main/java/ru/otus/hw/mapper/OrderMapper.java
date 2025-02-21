package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoWeb;
import ru.otus.hw.domain.Order;


@Component
public class OrderMapper {

    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getStartDate(),
                order.getEndDate(),
                order.getOrderField(),
                order.getUserId()
        );
    }

    public OrderDtoWeb toOrderDtoWeb(Order order, String message) {
        return new OrderDtoWeb(
                order.getId(),
                order.getStatus(),
                order.getStartDate(),
                order.getEndDate(),
                order.getOrderField(),
                order.getUserId(),
                message
        );
    }
}
