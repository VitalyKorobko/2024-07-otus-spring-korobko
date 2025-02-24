package ru.otus.hw.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.Order;

import java.util.Date;

@Service
@Slf4j
public class MailProcessorOrder implements MailProcessor<Order> {

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
