package ru.otus.hw.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityDto {
    private String id;

    @Positive(message = "Количество продукта целое число")
    private int productCount;

    @Size(min = 1, message = "id продукта не должно быть пустым")
    private String productId;

}
