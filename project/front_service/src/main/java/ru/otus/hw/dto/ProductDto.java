package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {

    private String id;

    @Size(min = 1, message = "Введите название товара")
    private String title;

    @Size(min = 1, message = "Введите артикул товара, минимум 6 символов")
    private String ref;

//    @Pattern(
//            regexp = "(?:https?:\\/\\/)?(?:[\\w\\.]+)",
//            message = "неверный формат ссылки"
//    )
    private String image;

    @Size(min = 1, max = 50,  message = "Длина description не менее одного и не более 50 символов")
    private String description;

    @PositiveOrZero(message = "Введите стоимость")
    private int price;

    private long sellerId;

    @JsonCreator
    public ProductDto(@JsonProperty("id") String id,
                      @JsonProperty("title") String title,
                      @JsonProperty("ref") String ref,
                      @JsonProperty("image") String image,
                      @JsonProperty("description") String description,
                      @JsonProperty("price") int price,
                      @JsonProperty("sellerId") long sellerId) {
        this.id = id;
        this.title = title;
        this.ref = ref;
        this.image = image;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
    }

}
