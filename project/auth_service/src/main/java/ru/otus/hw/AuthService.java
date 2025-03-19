package ru.otus.hw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class AuthService {

    public static void main(String[] args) {
        SpringApplication.run(AuthService.class);

        log.info("http://localhost:7771 ");


    }

}
