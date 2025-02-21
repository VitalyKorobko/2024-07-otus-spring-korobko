package ru.otus.hw.models;

import ru.otus.hw.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private String id;

    private Status status;

    private Date startDate;

    private Date endDate;

    private String orderField;

    private User user;

    public Order(Status status, Date startDate, Date endDate, User user) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

}
