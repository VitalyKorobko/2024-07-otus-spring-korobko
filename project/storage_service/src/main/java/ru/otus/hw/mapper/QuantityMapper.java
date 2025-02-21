package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.QuantityDto;
import ru.otus.hw.dto.QuantityDtoWeb;
import ru.otus.hw.model.Quantity;


@Component
public class QuantityMapper {
    public QuantityDto toQuantityDto(Quantity product) {
        return new QuantityDto(
                product.getId(),
                product.getProductCount(),
                product.getProductId()
        );
    }

    public QuantityDtoWeb toQuantityDtoWeb(Quantity product, String message) {
        return new QuantityDtoWeb(
                product.getId(),
                product.getProductCount(),
                product.getProductId(),
                message
        );
    }


}
