package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        System.out.println("http://localhost:8080");
        System.out.println("http://localhost:9000");



    }

}
