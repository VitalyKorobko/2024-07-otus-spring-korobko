package ru.otus.hw.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.exception.ImpossibleSaveEntityException;
import ru.otus.hw.mapper.ProductMapper;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.User;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class ProductRepositoryImpl implements ProductRepository {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final RestClient productRestClient;

    private final TokenStorage tokenStorage;

    private final ProductMapper productMapper;

    private final UserRepository userRepository;

    public ProductRepositoryImpl(@Qualifier("productRestClient") RestClient productRestClient,
                                 TokenStorage tokenStorage, ProductMapper productMapper,
                                 UserRepository userRepository) {
        this.productRestClient = productRestClient;
        this.tokenStorage = tokenStorage;
        this.productMapper = productMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Retryable(retryFor = {Exception.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Product create(Product product) {
        ProductDto productDto = productRestClient.post()
                .uri("/api/v1/product")
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .body(productMapper.toProductDto(product))
                .retrieve()
                .body(ProductDto.class);
        if (isNull(productDto)) {
            throw new ImpossibleSaveEntityException("impossible to save product!");
        }
        User seller = userRepository.findById(productDto.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller with id %d not found"
                        .formatted(productDto.getSellerId())));
        return productMapper.toProduct(productDto, seller);
    }

    @Override
    @Retryable(retryFor = {Exception.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Product update(Product product) {
        ProductDto productDto = productRestClient.patch()
                .uri("/api/v1/product/%s".formatted(product.getId()))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .body(productMapper.toProductDto(product))
                .retrieve()
                .body(ProductDto.class);
        if (isNull(productDto)) {
            throw new ImpossibleSaveEntityException("impossible to save product!");
        }
        User seller = findUserById(productDto.getSellerId());
        return productMapper.toProduct(productDto, seller);
    }

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public List<Product> findAllByUser(User user) {
        List<ProductDto> productDtoList = productRestClient.get()
                .uri("/api/v1/product")
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductDto>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
        return productDtoList.stream()
                .filter(productDto -> productDto.getSellerId() == user.getId())
                .map((productDto -> productMapper.toProduct(productDto, user)))
                .toList();
    }

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public List<Product> findAll() {
        List<ProductDto> productDtoList = productRestClient.get()
                .uri("/api/v1/product")
//                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductDto>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
        return productDtoList.stream().
                map((productDto -> productMapper.toProduct(productDto, null)))
                .toList();
    }

    @Override
    @Retryable(retryFor = {Exception.class}, noRetryFor = {ImpossibleSaveEntityException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public Optional<Product> findById(String id) {
        ProductDto productDto = productRestClient.get()
                .uri("/api/v1/product/%s".formatted(id))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve()
                .body(ProductDto.class);
        if (isNull(productDto)) {
            throw new ImpossibleSaveEntityException("impossible to save product!");
        }
        User seller = findUserById(productDto.getSellerId());
        return Optional.of(productMapper.toProduct(productDto, seller));
    }

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public void delete(Product product) {
        productRestClient.delete()
                .uri("/api/v1/product/%s".formatted(product.getId()))
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .retrieve();
    }

    private User findUserById(long sellerId) {
        return userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller with id %d not found"
                        .formatted(sellerId)));
    }
}
