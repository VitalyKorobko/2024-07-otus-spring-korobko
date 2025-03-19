package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.AppProps;
import ru.otus.hw.enums.OrderStatus;
import ru.otus.hw.model.Order;

@Service
@RequiredArgsConstructor
public class OrderToEmailTransformerImpl implements OrderToEmailTransformer {
    private final AppProps appProps;

    @Override
    public SimpleMailMessage transform(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appProps.getServerEmail());
        message.setTo(order.userEmail());
        message.setSubject(createMailSubject(order));
        message.setText(createText(order));
        return message;
    }

    private String createText(Order order) {
        if (order.status() == OrderStatus.ISSUED) {
            return ("Hello, %s!%n Your order %s was created.%n" +
                    "Order info: %s.%n Order status is %s. %n Best regards, Market")
                    .formatted(order.userEmail(), order.id(), order.orderField(), order.status().name());
        } else {
            return "Hello, %s!%n Status of your order: %s was changed: %s. %n Best regards, Market"
                    .formatted(order.userEmail(), order.id(), order.status().name());
        }
    }

    private String createMailSubject(Order order) {
        return "order number: " + order.id();
    }
}
