package ru.otus.hw.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.Order;

import java.util.Date;

@Service("dataProcessorMono")
public class DataProcessorOrderReactorMono implements DataProcessor<Order> {
    private static final Logger log = LoggerFactory.getLogger(DataProcessorOrderReactorMono.class);

    @Override
    public Order process(Order order) {
        log.info("processor with order: {}", order);
        var newOrder = new Order(
                order.id(),
                order.status(),
                order.startDate(),
                new Date(),
                order.orderField(),
                order.customerId(),
                order.userEmail(),
                order.username()
        );
        log.info("processor create new Order with date {}", newOrder);
        return newOrder;
    }

}
