package ru.otus.hw.sender;

import ru.otus.hw.model.Order;

public interface MailSender<T> {
    T send(Order order);

}
