package ru.otus.hw.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;
import ru.otus.hw.enums.Status;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.MailRepository;
import ru.otus.hw.repositories.OrderRepository;
import ru.otus.hw.repositories.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final MailRepository mailRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("order with id %d not found".formatted(id)));
    }

    @Override
    public Order save(Order order) {
        Order returnedOrder;
        if (isNull(order.getId())) {
            returnedOrder = orderRepository.create(order);
        } else {
            returnedOrder = orderRepository.update(order);
        }
        if (returnedOrder.getStatus() == Status.CURRENT) {
            return returnedOrder;
        }
        //здесь отправляем сообщение mail-client
        mailRepository.save(returnedOrder);
        return returnedOrder;
    }

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
    public Order saveOrderAsJson(Order order, List<String> productId, List<Integer> count) {
        Map<String, Integer> map;
        String jsonString;
        try {
            map = objectMapper.readValue(order.getOrderField(), HashMap.class);
            for (int i = 0; i < productId.size(); i++) {
                map.put(productId.get(i), count.get(i));
            }
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error when deserializing order with id: %s".formatted(order.getId()));
            throw new EntityNotFoundException("Error when deserializing order with id: %s"
                    .formatted(order.getId()));
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
            currentOrder = new Order(Status.CURRENT, LocalDateTime.now(), LocalDateTime.now(), user);
            map.put(String.valueOf(productId), 1);
            try {
                jsonString = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                log.error("Error when deserializing order with id: %s".formatted(currentOrder.getId()));
                throw new EntityNotFoundException("Error when deserializing order with id: %s"
                        .formatted(currentOrder.getId()));
            }
        } else {
            jsonString = serializeOrder(currentOrder, productId, user);
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
            log.error("Error when deserializing order with id: %s".formatted(order.getId()));
            throw new EntityNotFoundException("Error when deserializing order with id: %s"
                    .formatted(order.getId()));
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
            log.error("Error when deserializing order with id: %s".formatted(order.getId()));
            throw new EntityNotFoundException("Error when deserializing order with id: %s"
                    .formatted(order.getId()));
        }
        Map<Product, Integer> mapProducts = new HashMap<>();
        for (String productId : map.keySet()) {
            var key = productRepository.findById(productId);
            if (key.isPresent()) {
                mapProducts.put(productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("product with id %s not found"
                                .formatted(productId))), map.get(productId));
            }
        }
        return mapProducts;
    }

    //общая стоимость заказа
    private int getTotal(Map<Product, Integer> mapProducts) {
        int total = 0;
        for (Product product : mapProducts.keySet()) {
            if (product == null) {
                continue;
            }
            total += product.getPrice() * mapProducts.get(product);
        }
        return total;
    }

    private String serializeOrder(Order order, String productId, User user) {
        Map<String, Integer> map = new HashMap<>();
        String jsonString;
        try {
            map = objectMapper.readValue(order.getOrderField(), HashMap.class);
            int count;
            if (map.get(String.valueOf(productId)) == null) {
                count = 0;
            } else {
                count = map.get(String.valueOf(productId));
            }
            map.put(String.valueOf(productId), ++count);
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error when deserializing order with id: %s".formatted(order.getId()));
            throw new EntityNotFoundException("Error when deserializing order with id: %s"
                    .formatted(order.getId()));
        }
        return jsonString;
    }
}

