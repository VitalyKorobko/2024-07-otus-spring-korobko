//package ru.otus.hw.services;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import ru.otus.hw.dto.ProductDto;
//import ru.otus.hw.models.Product;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//public interface ProductService {
//    Mono<Product> findById(String id);
//
//    Flux<Product> findAll();
//
//    Mono<Product> insert(String title, String authorId, Set<String> genreIds);
//
//    Mono<Product> update(String id, String title, String authorId, Set<String> genreIds);
//
//    Mono<Void> deleteById(String id);
//}
