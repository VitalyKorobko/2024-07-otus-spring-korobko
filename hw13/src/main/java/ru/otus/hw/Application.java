package ru.otus.hw;

import jakarta.annotation.PostConstruct;
import org.h2.tools.Console;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.hw.enums.RoleList;
import ru.otus.hw.services.UserService;

import java.sql.SQLException;
import java.util.Set;

@SpringBootApplication
public class Application {

    @Autowired
    UserService userService;

    @PostConstruct
    public void init(){
        userService.insert("user", "1234", (short) 33, Set.of(RoleList.USER.getValue()));
        userService.insert("publisher",  "1234", (short) 41, Set.of(RoleList.PUBLISHER.getValue()));
    }

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Application.class);
        System.out.println("http://localhost:8080\nadmin:password");

        Console.main(args);


    }

}
