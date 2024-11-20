package ru.otus.hw;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.h2.tools.Console;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.sql.SQLException;

@SpringBootApplication
public class Application {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public Application(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void init() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), true, null));
    }

    public static void main(String[] args) throws SQLException {
        var ctx = SpringApplication.run(Application.class);
        System.out.printf("http://localhost:%s/", ctx.getEnvironment().getProperty("server.port"));
//        Console.main(args);


    }

}
