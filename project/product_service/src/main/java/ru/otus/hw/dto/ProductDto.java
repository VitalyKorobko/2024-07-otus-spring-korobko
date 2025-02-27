package ru.otus.hw.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String id;

    @Size(min = 3, message = "название продукта должно быть более 3 сиволов")
    private String title;

    @Size(min = 4, message = "введите артикул продукта, минимум 4 сивола")
    private String ref;

    @Pattern(regexp = "(^(http:)|(https:).+)",
    message = "в поле image должна быть ссылка на картинку")
    private String image;

    @Size(max = 255, message = "Поле описание товара не более 255 сиволов")
    private String description;

    @PositiveOrZero(message = "Цена - любое целое число")
    private int price;

    @PositiveOrZero(message = "id - продавца целое число")
    private long sellerId;

}
