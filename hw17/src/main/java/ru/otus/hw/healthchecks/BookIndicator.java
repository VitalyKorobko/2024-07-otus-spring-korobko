package ru.otus.hw.healthchecks;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookService;

@Component
public class BookIndicator implements HealthIndicator {
    private final BookService bookService;

    public BookIndicator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public Health health() {
        var amount = bookService.findAll().size();
        if (amount == 0) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Не найдено ни одной книги!")
                    .build();
        } else {
            return Health.up()
                    .withDetail("message", "Количество книг %d".formatted(amount))
                    .build();
        }
    }
}

