package ru.otus.hw.model;

import ru.otus.hw.enums.OrderStatus;

public record Order(
        String id,

        OrderStatus status,

        String startDate,

        String endDate,

        String orderField,

        String customerId,

        String userEmail,

        String username) {
}

