package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.enums.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoWeb {
    private String id;

    private Status status;

    private long startDate;

    private long endDate;

    private String orderField;

    private long userId;

    private String message;

}
