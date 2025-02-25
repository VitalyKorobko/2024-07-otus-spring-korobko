package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.enums.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Order {
    @Id
    private String id;

    private Status status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String orderField;

    private long userId;


}
