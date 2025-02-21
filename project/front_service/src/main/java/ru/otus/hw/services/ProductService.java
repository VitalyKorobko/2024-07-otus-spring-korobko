package ru.otus.hw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.hw.enums.Status;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    Product create(Product product);

    Product update(Product product);

    List<Product> findAllByUser(User user);

    List<Product> findAll();

    Product findById(String id);

    void delete(Product product);

    boolean checkFieldLength(String string);

    boolean checkUrl(String stringUrl);

    boolean checkProductByCart(String product_id, User user);

    boolean checkPrice(String price);

}
