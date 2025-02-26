package ru.otus.example.facadegateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           @Value("${app.host}") String host,
                                           @Value("${app.auth-port}") String authPort,
                                           @Value("${app.product-port}") String productPort,
                                           @Value("${app.order-port}") String orderPort,
                                           @Value("${app.storage-port}") String storagePort,
                                           @Value("${app.mail-client-port}") String mailClientPort,
                                           @Value("${app.mail-processor-port}") String mailProcessorPort,
                                           @Value("${app.notification-port}") String notificationPort) {
        return builder.routes()
                //auth_service
                .route(p -> p.path("/auth/api/v1/auth")
                        .filters(f -> f.rewritePath("/auth/api/v1/auth", "/api/v1/auth")
                        ).uri("%s:%s/".formatted(host, authPort)))
                .route(p -> p.path("/auth/api/v1/auth/*")
                        .filters(f -> f.rewritePath("/auth/api/v1/auth/(?<segment>.*)",
                                "/api/v1/auth/${segment}")).uri("%s:%s/".formatted(host, authPort)))
                .route(p -> p.path("/auth/api/v1/token")
                        .filters(f -> f.rewritePath("/auth/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, authPort)))
                //product_service
                .route(p -> p.path("/product/api/v1/product")
                        .filters(f -> f.rewritePath("/product/api/v1/product", "/api/v1/product")
                        ).uri("%s:%s/".formatted(host, productPort)))
                .route(p -> p.path("/product/api/v1/product/*")
                        .filters(f -> f.rewritePath("/product/api/v1/product/(?<segment>.*)",
                                "/api/v1/product/${segment}")).uri("%s:%s/".formatted(host, productPort)))
                .route(p -> p.path("/product/api/v1/token")
                        .filters(f -> f.rewritePath("/product/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, productPort)))
                //order_service
                .route(p -> p.path("/order/api/v1/order")
                        .filters(f -> f.rewritePath("/order/api/v1/order", "/api/v1/order")
                        ).uri("%s:%s/".formatted(host, orderPort)))
                .route(p -> p.path("/order/api/v1/order/*")
                        .filters(f -> f.rewritePath("/order/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("%s:%s/".formatted(host, orderPort)))
                .route(p -> p.path("/order/api/v1/token")
                        .filters(f -> f.rewritePath("/order/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, orderPort)))
                //storage_service
                .route(p -> p.path("/storage/api/v1/quantity")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity", "/api/v1/quantity")
                        ).uri("%s:%s/".formatted(host, storagePort)))
                .route(p -> p.path("/storage/api/v1/quantity/*")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity/(?<segment>.*)",
                                "/api/v1/quantity/${segment}")).uri("%s:%s/".formatted(host, storagePort)))
                .route(p -> p.path("/storage/api/v1/token")
                        .filters(f -> f.rewritePath("/storage/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, storagePort)))
                //mail_client
                .route(p -> p.path("/mail_client/api/v1/order")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/order", "/api/v1/order")
                        ).uri("%s:%s/".formatted(host, mailClientPort)))
                .route(p -> p.path("/mail_client/api/v1/order/*")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("%s:%s/".formatted(host, mailClientPort)))
                .route(p -> p.path("/mail_client/api/v1/token")
                        .filters(f -> f.rewritePath("/mail_client/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, mailClientPort)))
                //mail_processor
                .route(p -> p.path("/mail_processor/api/v1/order")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/order", "/api/v1/order")
                        ).uri("%s:%s/".formatted(host, mailProcessorPort)))
                .route(p -> p.path("/mail_processor/api/v1/order/*")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("%s:%s/".formatted(host, mailProcessorPort)))
                .route(p -> p.path("/mail_processor/api/v1/token")
                        .filters(f -> f.rewritePath("/mail_processor/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, mailProcessorPort)))
                //notification_service
                .route(p -> p.path("/notification/api/v1/order")
                        .filters(f -> f.rewritePath("/notification/api/v1/order", "/api/v1/order")
                        ).uri("%s:%s/".formatted(host, notificationPort)))
                .route(p -> p.path("/notification/api/v1/order/*")
                        .filters(f -> f.rewritePath("/notification/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("%s:%s/".formatted(host, notificationPort)))
                .route(p -> p.path("/notification/api/v1/token")
                        .filters(f -> f.rewritePath("/notification/api/v1/token", "/api/v1/token")
                        ).uri("%s:%s/".formatted(host, notificationPort)))
                .build();

    }

}
