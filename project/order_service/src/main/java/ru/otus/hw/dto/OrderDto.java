package ru.otus.hw.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String id;

    @Pattern(regexp = ("^(CURRENT)|(ISSUED)|(PAID)|(COMPLETED)$"),
            message = "Статус должен быть выбран из четырех вариантов CURRENT, ISSUED, PAID, COMPLETED")
    private String status;

    @Positive(message = "начальная дата в секундах от 1970-01-01T00:00:00, макс. значение 365241780471")
    private long startDate;

    @Positive(message = "дата изменения в секундах от 1970-01-01T00:00:00, макс. значение 365241780471")
    private long endDate;

    @Size(message = "Поле orderField не должно быть пустым")
    private String orderField;

    @PositiveOrZero(message = "Должно быть целое положительное число")
    private long userId;


}
