package ru.otus.hw.repositories;

import ru.otus.hw.dto.OrderDtoForMail;
import ru.otus.hw.models.Order;

public interface MailRepository {
    OrderDtoForMail save(Order order);

}
