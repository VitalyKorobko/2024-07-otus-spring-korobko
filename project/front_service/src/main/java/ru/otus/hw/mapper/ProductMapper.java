package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.User;

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
                product.getUser().getId()
        );
    }

    public Product toProduct(ProductDto productDto, User seller) {
        return new Product(
                productDto.getId(),
                productDto.getTitle(),
                productDto.getRef(),
                productDto.getImage(),
                productDto.getDescription(),
                productDto.getPrice(),
                seller
        );
    }
}
