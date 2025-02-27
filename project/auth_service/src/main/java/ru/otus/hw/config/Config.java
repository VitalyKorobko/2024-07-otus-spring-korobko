package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class Config {
    @Bean
    public TokenStorage tokenStorage() {
        return new TokenStorage();
    }

    @Bean
    public RestClient getClient() {
        return RestClient.create();
    }

    @Bean
    public ThreadPoolExecutor poolExecutor() {
        return new ThreadPoolExecutor(1, 2,
                30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }


}
