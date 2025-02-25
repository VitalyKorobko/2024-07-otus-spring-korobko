package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.enums.Status;

@Data
@NoArgsConstructor
public class OrderDto {
    private String id;

    private Status status;

    private long startDate;

    private long endDate;

    private String orderField;

    private long userId;

    @JsonCreator
    public OrderDto(@JsonProperty("id") String id,
                    @JsonProperty("status") Status status,
                    @JsonProperty("startDate") long startDate,
                    @JsonProperty("endDate") long endDate,
                    @JsonProperty("orderField") String orderField,
                    @JsonProperty("userId") long userId) {
        this.id = id;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderField = orderField;
        this.userId = userId;
    }
}
