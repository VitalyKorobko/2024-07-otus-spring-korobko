package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.otus.hw.services.TokenService;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AppConfig {

    @Bean(name = "authRestClient")
    public RestClient authRestClient(@Value("${app.gateway.host}") String host,
                                     @Value("${app.gateway.auth-path}") String path) {
        return RestClient.create(String.format("%s/%s", host, path));
    }

    @Bean(name = "productRestClient")
    public RestClient productRestClient(@Value("${app.gateway.host}") String host,
                                        @Value("${app.gateway.product-path}") String path) {
        return RestClient.create(String.format("%s/%s", host, path));
    }

    @Bean(name = "storageRestClient")
    public RestClient storageRestClient(@Value("${app.gateway.host}") String host,
                                        @Value("${app.gateway.storage-path}") String path) {
        return RestClient.create(String.format("%s/%s", host, path));
    }

    @Bean(name = "mailRestClient")
    public RestClient mailRestClient(@Value("${app.gateway.host}") String host,
                                     @Value("${app.gateway.notification-path}") String path) {
        return RestClient.create(String.format("%s/%s", host, path));
    }

    @Bean(name = "orderRestClient")
    public RestClient orderRestClient(@Value("${app.gateway.host}") String host,
                                      @Value("${app.gateway.order-path}") String path) {
        return RestClient.create(String.format("%s/%s", host, path));
    }

    @Bean(name = "testRestClient")
    public RestClient testRestClient() {
        return RestClient.create();
    }

    @Bean
    TokenStorage tokenStorage(TokenService tokenService) {
        return new TokenStorage();
    }
}
