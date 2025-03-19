package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    private String id;

    private String title;

    private String ref;

    private String image;

    private String description;

    private int price;

    private User user;

    public Product(String title, String ref, String image, String description, int price, User user) {
        this.title = title;
        this.ref = ref;
        this.image = image;
        this.description = description;
        this.price = price;
        this.user = user;
    }
}
