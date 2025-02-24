package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {
    @Bean
    TokenStorage getToken() {
        return new TokenStorage();
    }


}
