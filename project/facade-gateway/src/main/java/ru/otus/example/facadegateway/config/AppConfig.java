package ru.otus.example.facadegateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.example.facadegateway.service.DiscoveryService;


@Configuration
public class AppConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           DiscoveryService service,
                                           @Value("${app.auth_service}") String auth,
                                           @Value("${app.mail_client}") String client,
                                           @Value("${app.mail_processor}") String processor,
                                           @Value("${app.notification_service}") String notification,
                                           @Value("${app.order_service}") String order,
                                           @Value("${app.product_service}") String product,
                                           @Value("${app.storage_service}") String storage) {
        return builder.routes()
                //auth_service
                .route(p -> p.path("/auth/api/v1/auth")
                        .filters(f -> f.rewritePath("/auth/api/v1/auth", "/api/v1/auth")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(auth), service.getPort(auth))))
                .route(p -> p.path("/auth/api/v1/auth/service/*")
                        .filters(f -> f.rewritePath("/auth/api/v1/auth/service/(?<segment>.*)",
                                "/api/v1/auth/service/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(auth), service.getPort(auth))))
                .route(p -> p.path("/auth/api/v1/auth/*")
                        .filters(f -> f.rewritePath("/auth/api/v1/auth/(?<segment>.*)",
                                "/api/v1/auth/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(auth), service.getPort(auth))))
                .route(p -> p.path("/auth/api/v1/token")
                        .filters(f -> f.rewritePath("/auth/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(auth), service.getPort(auth))))
                //product_service
                .route(p -> p.path("/product/api/v1/product")
                        .filters(f -> f.rewritePath("/product/api/v1/product", "/api/v1/product")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(product), service.getPort(product))))
                .route(p -> p.path("/product/api/v1/product/*")
                        .filters(f -> f.rewritePath("/product/api/v1/product/(?<segment>.*)",
                                "/api/v1/product/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(product), service.getPort(product))))
                .route(p -> p.path("/product/api/v1/token")
                        .filters(f -> f.rewritePath("/product/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(product), service.getPort(product))))
                .route(p -> p.path("/product/api/v1/tokens")
                        .filters(f -> f.rewritePath("/product/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(product), service.getPort(product))))
                //order_service
                .route(p -> p.path("/order/api/v1/order")
                        .filters(f -> f.rewritePath("/order/api/v1/order", "/api/v1/order")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(order), service.getPort(order))))
                .route(p -> p.path("/order/api/v1/order/*")
                        .filters(f -> f.rewritePath("/order/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(order), service.getPort(order))))
                .route(p -> p.path("/order/api/v1/token")
                        .filters(f -> f.rewritePath("/order/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(order), service.getPort(order))))
                .route(p -> p.path("/order/api/v1/tokens")
                        .filters(f -> f.rewritePath("/order/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(order), service.getPort(order))))
                //storage_service
                .route(p -> p.path("/storage/api/v1/quantity")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity", "/api/v1/quantity")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(storage), service.getPort(storage))))
                .route(p -> p.path("/storage/api/v1/quantity/*")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity/(?<segment>.*)",
                                "/api/v1/quantity/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(storage), service.getPort(storage))))
                .route(p -> p.path("/storage/api/v1/token")
                        .filters(f -> f.rewritePath("/storage/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(storage), service.getPort(storage))))
                .route(p -> p.path("/storage/api/v1/tokens")
                        .filters(f -> f.rewritePath("/storage/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(storage), service.getPort(storage))))
                //mail_client
                .route(p -> p.path("/mail_client/api/v1/order")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/order", "/api/v1/order")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(client), service.getPort(client))))
                .route(p -> p.path("/mail_client/api/v1/order/*")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(client), service.getPort(client))))
                .route(p -> p.path("/mail_client/api/v1/token")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(client), service.getPort(client))))
                .route(p -> p.path("/mail_client/api/v1/tokens")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(client), service.getPort(client))))
                //mail_processor
                .route(p -> p.path("/mail_processor/api/v1/order")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/order", "/api/v1/order")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(processor), service.getPort(processor))))
                .route(p -> p.path("/mail_processor/api/v1/order/*")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(processor), service.getPort(processor))))
                .route(p -> p.path("/mail_processor/api/v1/token")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(processor), service.getPort(processor))))
                .route(p -> p.path("/mail_processor/api/v1/tokens")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(processor), service.getPort(processor))))
                //notification_service
                .route(p -> p.path("/notification/api/v1/order")
                        .filters(f -> f.rewritePath("/notification/api/v1/order", "/api/v1/order")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(notification), service.getPort(notification))))
                .route(p -> p.path("/notification/api/v1/order/*")
                        .filters(f -> f.rewritePath("/notification/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("http://%s:%s/"
                                .formatted(service.getHostName(notification), service.getPort(notification))))
                .route(p -> p.path("/notification/api/v1/token")
                        .filters(f -> f.rewritePath("/notification/api/v1/token", "/api/v1/token")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(notification), service.getPort(notification))))
                .route(p -> p.path("/notification/api/v1/tokens")
                        .filters(f -> f.rewritePath("/notification/api/v1/tokens", "/api/v1/tokens")
                        ).uri("http://%s:%s/"
                                .formatted(service.getHostName(notification), service.getPort(notification))))
                .build();

    }

}
