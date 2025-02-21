package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String id;

    private String title;

    private String ref;

    private String image;

    private String description;

    private int price;

    private long sellerId;

}
