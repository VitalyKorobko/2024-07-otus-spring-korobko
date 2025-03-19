package ru.otus.hw.controller.rest;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.exception.NotAvailableException;
import ru.otus.hw.exception.ServiceNotFoundException;
import ru.otus.hw.service.TokenService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@RestController
@Slf4j
public class AuthController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final TokenService tokenService;

    private final String host;

    private final String port;

    private final ThreadPoolExecutor executor;

    private final EurekaClient eurekaClient;

    private final ScheduledExecutorService scheduledExecutorService;

    public AuthController(TokenService tokenService,
                          @Value("${app.gateway.host}") String host,
                          @Value("${app.gateway.port}") String port,
                          ThreadPoolExecutor executor,
                          EurekaClient eurekaClient,
                          ScheduledExecutorService scheduledExecutorService) {
        this.tokenService = tokenService;
        this.host = host;
        this.port = port;
        this.executor = executor;
        this.eurekaClient = eurekaClient;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @CircuitBreaker(name = "setTokenCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
    @PostMapping("/api/v1/auth")
    public void regService(Authentication authentication) {
        var token = tokenService.add(authentication);
        send(token);
    }


    @GetMapping("/api/v1/auth/service/{serviceName}")
    public void initService(@PathVariable("serviceName") String serviceName) {
        log.info("init request from {}", serviceName);
        scheduledExecutorService.schedule(() -> {
            var app = getApp(serviceName);
            log.info("get app: {}", app);
            tokenService.sendAll("%s:%s/%s/api/v1/tokens".formatted(host, port, serviceName));
        }, 5, TimeUnit.SECONDS);

    }

    @Retryable(retryFor = {ServiceNotFoundException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    private Application getApp(String serviceName) {
        var app = eurekaClient.getApplication(serviceName);
        if (isNull(app)) {
            throw new ServiceNotFoundException("Ещё не зарегистрирован");
        }
        return app;
    }

    private void send(String token) {
        log.info("token was sent: %s".formatted(token));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "product")));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "order")));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "storage")));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "mail_client")));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "mail_processor")));
        executor.execute(() ->
                tokenService.send(token, "%s:%s/%s/api/v1/token".formatted(host, port, "notification")));
    }

    private void circuitBreakerFallBack(Authentication authentication, Throwable e) {
        log.error("circuit breaker got open state: Err: {}:{}", e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }
}
