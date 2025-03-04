package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.hw.services.DiscoveryService;

@Configuration
@Slf4j
public class Config {

    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               @Value("${application.auth_service.name}") String serviceName,
                               DiscoveryService discoveryService) {
        var url = "http://" + discoveryService.getHostName(serviceName) + ":" + discoveryService.getPort(serviceName);
        log.info("URL: %s".formatted(url));
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public RestClient authWebClient(@Value("${application.auth_service.name}") String serviceName,
                                    DiscoveryService discoveryService) {
        var url = "http://" + discoveryService.getHostName(serviceName) + ":" + discoveryService.getPort(serviceName);
        log.info("URL: %s".formatted(url));
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }



}
