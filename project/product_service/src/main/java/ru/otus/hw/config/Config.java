package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    TokenStorage tokenStorage() {
        return new TokenStorage();
    }


}
