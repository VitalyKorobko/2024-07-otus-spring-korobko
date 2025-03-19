package ru.otus.hw.service;

import org.springframework.mail.SimpleMailMessage;
import ru.otus.hw.model.Order;

public interface OrderToEmailTransformer {
    SimpleMailMessage transform(Order order);
}
