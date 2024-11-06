package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(Application.class);
        System.out.printf("http://localhost:%s/", ctx.getEnvironment().getProperty("server.port"));



    }

}
