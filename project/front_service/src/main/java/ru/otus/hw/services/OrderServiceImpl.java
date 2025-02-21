package ru.otus.hw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.dto.OrderDto;
import ru.otus.hw.dto.OrderDtoForMail;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.exception.ImpossibleSaveEntityException;
import ru.otus.hw.mapper.OrderMapper;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;
import ru.otus.hw.enums.Status;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.OrderRepository;
import ru.otus.hw.repositories.ProductRepository;

import java.util.*;

import static java.util.Objects.isNull;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final ObjectMapper objectMapper;

    private final RestClient mailRestClient;

    private final TokenStorage tokenStorage;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
                            ObjectMapper objectMapper, @Qualifier("mailRestClient") RestClient mailRestClient,
                            TokenStorage tokenStorage, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
        this.mailRestClient = mailRestClient;
        this.tokenStorage = tokenStorage;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("order with id %d not found".formatted(id)));
    }

    @Override
    @Transactional
    public Order save(Order order) {
        Order returnedOrder;
        if (isNull(order.getId())) {
            returnedOrder = orderRepository.create(order);
        } else {
            returnedOrder = orderRepository.update(order);
        }
        if (returnedOrder.getStatus()==Status.CURRENT) {
            return returnedOrder;
        }
        //todo здесь отправляем сообщение mail-client
        var orderDtoForMail = mailRestClient.post()
                .uri("/api/v1/order")
                .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                .body(orderMapper.toOrderDtoForMail(returnedOrder))
                .retrieve();
//                .body(OrderDtoForMail.class);
        if (isNull(orderDtoForMail)) {
            throw new ImpossibleSaveEntityException("impossible to save order!");
        }
//        var customer = returnedOrder.getUser();
//        return orderRepository.update(orderMapper.toOrder(orderDtoForMail, customer));
        return returnedOrder;
    }

//    @Override
//    public Order create(Order order) {
//        return orderRepository.create(order);
//    }
//
//    @Override
//    public Order update(Order order) {
//        return orderRepository.update(order);
//    }

    @Override
    public Order findByUserAndStatus(User user, Status status) {
        var orders = orderRepository.findByUser(user);
        var order = orders.stream()
                .filter(o -> Objects.equals(o.getStatus().name(), status.name()))
                .findFirst();
        return order.orElse(null);
    }

    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    //получаем мапу (товар - количество) для корзины
    @Override
    public Map<Product, Integer> getMapProductsByCart(Order currentOrder) {
        if (currentOrder == null) {
            return new HashMap<>();
        }
        return getMapProducts(currentOrder);
    }

    //получаем мапу (товар - количество) для сформированного заказа
    @Override
    public Map<Product, Integer> getProductsByOrder(Order order) {
        return getMapProducts(order);
    }

    //сохраняем данные о заказе в формате json
    @Override
    public Order saveOrderAsJson(Order order, List<String> product_id, List<Integer> count) {
        Map<String, Integer> map;
        String jsonString;
        try {
            map = objectMapper.readValue(order.getOrderField(), HashMap.class);
            for (int i = 0; i < product_id.size(); i++) {
                map.put(product_id.get(i), count.get(i));
            }
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        order.setOrderField(jsonString);
        return order;
    }

    //добавляем позицию в корзину
    @Override
    public Order addProductInCart(Order currentOrder, String productId, User user) {
        Map<String, Integer> map = new HashMap<>();
        String jsonString;
        if (currentOrder == null) {
            currentOrder = new Order(Status.CURRENT, new Date(), null, user);
            map.put(String.valueOf(productId), 1);
            try {
                jsonString = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                map = objectMapper.readValue(currentOrder.getOrderField(), HashMap.class);
                int count;
                if (map.get(String.valueOf(productId)) == null) {
                    count = 0;
                } else {
                    count = map.get(String.valueOf(productId));
                }
                map.put(String.valueOf(productId), ++count);
                jsonString = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        currentOrder.setOrderField(jsonString);
        return currentOrder;
    }

    //удаляем позицию из корзины
    @Override
    public Order deleteProductFromCart(Order order, String productId) {
        Map<String, Integer> map;
        String jsonString;
        try {
            map = objectMapper.readValue(order.getOrderField(), HashMap.class);
            map.remove(productId);
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        order.setOrderField(jsonString);
        return order;
    }

    //возвращаем общую стоимость текущего заказа(корзины) или любого другого заказа
    @Override
    public int getTotalByCartOrOrder(Map<Product, Integer> mapProducts) {
        return getTotal(mapProducts);
    }

    //получаем мапу из json-строки объекта order
    private Map<Product, Integer> getMapProducts(Order order) {
        Map<String, Integer> map;
        String jsonString = order.getOrderField();
        try {
            map = objectMapper.readValue(jsonString, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<Product, Integer> mapProducts = new HashMap<>();
        for (String product_id : map.keySet()) {
            var key = productRepository.findById(product_id);
            if (key.isPresent()) {
                mapProducts.put(productRepository.findById(product_id)
                        .orElseThrow(() -> new EntityNotFoundException("product with id %s not found"
                                .formatted(product_id))), map.get(product_id));
            }
        }
        return mapProducts;
    }

    //общая стоимость заказа
    private int getTotal(Map<Product, Integer> mapProducts) {
        int total = 0;
        for (Product product : mapProducts.keySet()) {
            if (product == null) continue;
            total += product.getPrice() * mapProducts.get(product);
        }
        return total;
    }
}

