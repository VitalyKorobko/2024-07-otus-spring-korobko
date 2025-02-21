package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.otus.hw.services.TokenService;

@Configuration
public class AppConfig {

    @Bean(name = "authRestClient")
    public RestClient authRestClient() {
        return RestClient.create(String.format("http://localhost:%d", 7771));
    }

    @Bean(name = "productRestClient")
    public RestClient productRestClient() {
        return RestClient.create(String.format("http://localhost:%d", 7773));
    }

    @Bean(name = "storageRestClient")
    public RestClient storageRestClient() {
        return RestClient.create(String.format("http://localhost:%d", 7774));
    }

    @Bean(name = "mailRestClient")
    public RestClient mailRestClient() {
        return RestClient.create(String.format("http://localhost:%d", 7775));
    }

    @Bean(name = "orderRestClient")
    public RestClient orderRestClient() {
        return RestClient.create(String.format("http://localhost:%d", 7778));
    }

    @Bean(name = "testRestClient")
    public RestClient testRestClient() {
        return RestClient.create();
    }

    @Bean
    TokenStorage getToken(TokenService tokenService) {
        return new TokenStorage();
    }
}
