package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDtoWeb {
    private String id;

    private String title;

    private String ref;

    private String image;

    private String description;

    private int price;

    private long sellerId;

    private String message;

}
