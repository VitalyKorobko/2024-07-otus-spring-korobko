package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class);

        System.out.println("http://localhost:7778");

    }
}
