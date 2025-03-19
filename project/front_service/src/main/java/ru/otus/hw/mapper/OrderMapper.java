package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoForMail;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class OrderMapper {
    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getStartDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getEndDate().toInstant(ZoneOffset.UTC).getEpochSecond(),
                order.getOrderField(),
                order.getUser().getId()
        );
    }

    public Order toOrder(OrderDto orderDto, User seller) {
        return new Order(
                orderDto.getId(),
                orderDto.getStatus(),
                LocalDateTime.ofEpochSecond(orderDto.getStartDate(), 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(orderDto.getEndDate(), 0, ZoneOffset.UTC),
                orderDto.getOrderField(),
                seller
        );
    }

    public OrderDtoForMail toOrderDtoForMail(Order order) {
        return new OrderDtoForMail(
                order.getId(),
                order.getStatus(),
                order.getStartDate().toString(),
                order.getEndDate().toString(),
                order.getOrderField(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getUser().getUsername()
        );
    }

}
