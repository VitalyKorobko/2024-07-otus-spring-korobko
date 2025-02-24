package ru.otus.hw.model;

import ru.otus.hw.enums.OrderStatus;

import java.util.Date;

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

