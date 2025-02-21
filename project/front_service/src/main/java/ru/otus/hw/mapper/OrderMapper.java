package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoForMail;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;

@Component
public class OrderMapper {
    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getStartDate(),
                order.getEndDate(),
                order.getOrderField(),
                order.getUser().getId()
        );
    }

    public Order toOrder(OrderDto orderDto, User seller) {
        return new Order(
                orderDto.getId(),
                orderDto.getStatus(),
                orderDto.getStartDate(),
                orderDto.getEndDate(),
                orderDto.getOrderField(),
                seller
        );
    }

    public OrderDtoForMail toOrderDtoForMail(Order order) {
        return new OrderDtoForMail(
                order.getId(),
                order.getStatus(),
                order.getStartDate(),
                order.getEndDate(),
                order.getOrderField(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getUser().getUsername()
        );
    }

    public Order toOrder(OrderDtoForMail orderDtoForMail, User customer) {
        return new Order(
                orderDtoForMail.getId(),
                orderDtoForMail.getStatus(),
                orderDtoForMail.getStartDate(),
                orderDtoForMail.getEndDate(),
                orderDtoForMail.getOrderField(),
                customer
        );
    }
}
