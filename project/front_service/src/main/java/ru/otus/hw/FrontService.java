package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class FrontService {

    public static void main(String[] args) {
        SpringApplication.run(FrontService.class, args);

        System.out.println("http://localhost:7772");
    }

}
