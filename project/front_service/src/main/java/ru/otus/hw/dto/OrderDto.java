package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.enums.Status;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderDto {
    private String id;

    private Status status;

    private Date startDate;

    private Date endDate;

    private String orderField;

    private long userId;

    @JsonCreator
    public OrderDto(@JsonProperty("id") String id,
                    @JsonProperty("status") Status status,
                    @JsonProperty("startDate") Date startDate,
                    @JsonProperty("endDate") Date endDate,
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
