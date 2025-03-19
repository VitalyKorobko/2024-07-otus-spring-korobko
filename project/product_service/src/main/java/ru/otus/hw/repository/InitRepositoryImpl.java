package ru.otus.hw.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitRepositoryImpl implements InitRepository {
    private final RestClient restClient;

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public void init(String serviceName) {
        log.info("init request from {}", serviceName);
        restClient.get()
                .uri("/api/v1/auth/service/%s".formatted(serviceName))
                .retrieve();
    }

}
