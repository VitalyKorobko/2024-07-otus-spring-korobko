package ru.otus.hw.models;

import ru.otus.hw.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private String id;

    private Status status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String orderField;

    private User user;

    public Order(Status status, LocalDateTime startDate, LocalDateTime endDate, User user) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

}
