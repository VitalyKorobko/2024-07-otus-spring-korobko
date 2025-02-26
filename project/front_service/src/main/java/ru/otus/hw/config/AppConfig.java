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

//    @Bean(name = "authRestClient")
//    public RestClient authRestClient() {
//        return RestClient.create(String.format("http://localhost:%d", 7771));
//    }
//
//    @Bean(name = "productRestClient")
//    public RestClient productRestClient() {
//        return RestClient.create(String.format("http://localhost:%d", 7773));
//    }
//
//    @Bean(name = "storageRestClient")
//    public RestClient storageRestClient() {
//        return RestClient.create(String.format("http://localhost:%d", 7774));
//    }
//
//    @Bean(name = "mailRestClient")
//    public RestClient mailRestClient() {
//        return RestClient.create(String.format("http://localhost:%d", 7775));
//    }
//
//    @Bean(name = "orderRestClient")
//    public RestClient orderRestClient() {
//        return RestClient.create(String.format("http://localhost:%d", 7778));
//    }

    @Bean(name = "testRestClient")
    public RestClient testRestClient() {
        return RestClient.create();
    }

    @Bean
    TokenStorage tokenStorage(TokenService tokenService) {
        return new TokenStorage();
    }
}
