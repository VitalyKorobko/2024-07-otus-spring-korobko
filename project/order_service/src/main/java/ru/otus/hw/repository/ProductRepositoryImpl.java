//package ru.otus.hw.repository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ProductRepositoryImpl implements ProductRepository{
//
//    private final WebClient webClient;
//
//    @Override
//    public Flux<Product> findAllByIdIn(List<String> ids) {
//        return webClient.get().uri(String.format("/data/%s", ids))
//                .accept(MediaType.APPLICATION_NDJSON)
//                .retrieve()
//                .bodyToFlux(Product.class);
//    }
//
//    @Override
//    public Mono<Product> findById(String id) {
//        log.info("request for product");
//        return webClient.get().uri(String.format("http://localhost:7773/api/v1/product/%s", id))
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Product.class);
//    }
//}
//
