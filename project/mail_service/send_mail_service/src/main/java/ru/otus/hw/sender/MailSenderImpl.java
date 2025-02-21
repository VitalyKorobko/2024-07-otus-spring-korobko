package ru.otus.hw.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;
import ru.otus.hw.model.Order;
import ru.otus.hw.service.OrderToEmailTransformer;

import java.util.concurrent.TimeUnit;

@Service
public class MailSenderImpl implements MailSender<Order> {
    private static final Logger log = LoggerFactory.getLogger(MailSenderImpl.class);

    private final JavaMailSender javaMailSender;

    private final OrderToEmailTransformer transformer;

    private final Scheduler mailScheduler;

    public MailSenderImpl(JavaMailSender javaMailSender, OrderToEmailTransformer transformer,
                          @Qualifier("mailScheduler") Scheduler mailScheduler) {
        this.javaMailSender = javaMailSender;
        this.transformer = transformer;
        this.mailScheduler = mailScheduler;
    }

    @Override
    public Order send(Order order) {
        log.info("START sending email about the order: {}", order);
        mailScheduler.schedule(() -> {
            javaMailSender.send(transformer.transform(order));
            log.info("END sending email about the order: {}", order);
        }, 30, TimeUnit.SECONDS);
        return order;
    }


}
