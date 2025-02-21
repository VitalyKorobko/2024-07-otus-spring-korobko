package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.enums.Status;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderDtoForMail {
    private String id;

    private Status status;

    private Date startDate;

    private Date endDate;

    private String orderField;

    private long customerId;

    private String userEmail;

    private String username;

    @JsonCreator
    public OrderDtoForMail(@JsonProperty("id") String id,
                           @JsonProperty("status") Status status,
                           @JsonProperty("startDate") Date startDate,
                           @JsonProperty("endDate") Date endDate,
                           @JsonProperty("orderField") String orderField,
                           @JsonProperty("customerId") long customerId,
                           @JsonProperty("userEmail") String userEmail,
                           @JsonProperty("username") String username) {
        this.id = id;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderField = orderField;
        this.customerId = customerId;
        this.userEmail = userEmail;
        this.username = username;
    }
}
