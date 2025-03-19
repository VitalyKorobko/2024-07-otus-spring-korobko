package ru.otus.hw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.repository.InitRepository;

@Component
public class AppRunner implements CommandLineRunner {
    private final InitRepository repository;

    private final String serviceName;

    public AppRunner(InitRepository repository,
                     @Value("${spring.application.name}") String serviceName) {
        this.repository = repository;
        this.serviceName = serviceName;
    }


    @Override
    public void run(String... args) throws Exception {
        repository.init(serviceName);

    }
}
