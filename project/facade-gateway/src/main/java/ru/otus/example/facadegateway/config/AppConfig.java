package ru.otus.example.facadegateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //product_service
                .route(p -> p.path("/product/api/v1/product")
                        .filters(f -> f.rewritePath("/product/api/v1/product", "/api/v1/product")
                        ).uri("http://localhost:7773/"))
                .route(p -> p.path("/product/api/v1/product/*")
                        .filters(f -> f.rewritePath("/product/api/v1/product/(?<segment>.*)",
                                "/api/v1/product/${segment}")).uri("http://localhost:7773/"))
                //order_service
                .route(p -> p.path("/order/api/v1/order")
                        .filters(f -> f.rewritePath("/order/api/v1/order", "/api/v1/order")
                        ).uri("http://localhost:7778/"))
                .route(p -> p.path("/order/api/v1/order/*")
                        .filters(f -> f.rewritePath("/order/api/v1/order/(?<segment>.*)",
                                "/api/v1/order/${segment}")).uri("http://localhost:7778/"))
                //storage_service
                .route(p -> p.path("/storage/api/v1/quantity")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity", "/api/v1/quantity")
                        ).uri("http://localhost:7774/"))
                .route(p -> p.path("/storage/api/v1/quantity/*")
                        .filters(f -> f.rewritePath("/storage/api/v1/quantity/(?<segment>.*)",
                                "/api/v1/quantity/${segment}")).uri("http://localhost:7774/"))
                .build();
    }
}
