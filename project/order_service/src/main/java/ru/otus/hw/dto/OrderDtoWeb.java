package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.enums.Status;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoWeb {
    private String id;

    private Status status;

    private Date startDate;

    private Date endDate;

    private String orderField;

    private long userId;

    private String message;

}
