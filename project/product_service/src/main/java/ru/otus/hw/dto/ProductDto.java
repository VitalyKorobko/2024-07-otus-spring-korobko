package ru.otus.hw.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String id;

    @Size(min = 3)
    private String title;

    private String ref;

    private String image;

    private String description;

    private int price;

    private long sellerId;

}
