package ru.otus.hw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;
import ru.otus.hw.enums.Status;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.ProductRepository;
import ru.otus.hw.services.check.CheckService;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final OrderService orderService;

    private final CheckService checkService;

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    public ProductServiceImpl(ProductRepository productRepository, OrderService orderService,
                              CheckService checkService, ObjectMapper objectMapper,
                              @Qualifier("testRestClient") RestClient restClient) {
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.checkService = checkService;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    @Override
    public Product create(Product product) {
        return productRepository.create(product);
    }

    @Override
    public Product update(Product product) {
        return productRepository.update(product);
    }

    @Override
    public List<Product> findAllByUser(User user) {
        return productRepository.findAllByUser(user);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("product with id %d not found".formatted(id)));
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    //проверка длины строки в поле ввода, для полей varchar(255) в бд
    //todo заменить!
    @Override
    public boolean checkFieldLength(String string) {
        return checkService.checkFieldLength(string);
    }

    //проверка ответа URL, ссылка на фото товара
    //todo заменить!
    @Override
    public boolean checkUrl(String stringUrl) {
        try {
            restClient.get().uri(stringUrl).retrieve();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //проверка,что товар уже есть в корзине
    //todo заменить!
    @Override
    public boolean checkProductByCart(String product_id, User user) {
        Order currentOrder = orderService.findByUserAndStatus(user, Status.CURRENT);
        boolean contains = false;
        if (currentOrder != null) {
            String jsonString = currentOrder.getOrderField();
            Map<String, Integer> map;
            try {
                map = objectMapper.readValue(jsonString, HashMap.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (map.containsKey(product_id)) {
                contains = true;
            }
        }
        return contains;
    }

    //проверка что передается число
    //todo заменить!
    @Override
    public boolean checkPrice(String price) {
        return checkService.checkValue(price);
    }


}
