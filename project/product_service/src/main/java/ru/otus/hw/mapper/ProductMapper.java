package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.dto.ProductDtoWeb;
import ru.otus.hw.model.Product;


@Component
public class ProductMapper {

    public ProductDto toProductDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getTitle(),
                product.getRef(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                product.getSellerId()
        );
    }


    public ProductDtoWeb toProductDtoWeb(Product product, String message) {
        return new ProductDtoWeb(
                product.getId(),
                product.getTitle(),
                product.getRef(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                product.getSellerId(),
                message
        );
    }
}
