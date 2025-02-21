package ru.otus.hw;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongock
@SpringBootApplication
@EnableMongoRepositories
public class ProductService {

    public static void main(String[] args) {
        SpringApplication.run(ProductService.class);

        System.out.println("http://localhost:7773");


    }

}
