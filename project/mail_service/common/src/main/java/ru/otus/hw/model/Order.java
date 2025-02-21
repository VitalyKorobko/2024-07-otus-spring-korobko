package ru.otus.hw.model;

import ru.otus.hw.enums.OrderStatus;

import java.util.Date;
import java.util.List;

public record Order(
        String id,

        OrderStatus status,

        Date startDate,

        Date endDate,

        String orderField,

        String customerId,

        String userEmail,

        String username) {
}

