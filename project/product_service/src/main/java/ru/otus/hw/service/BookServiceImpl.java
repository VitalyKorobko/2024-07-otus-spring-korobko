//package ru.otus.hw.services;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import ru.otus.hw.dto.ProductDto;
//import ru.otus.hw.exceptions.EntityNotFoundException;
//import ru.otus.hw.logging.LogCustom;
//import ru.otus.hw.mapper.ProductMapper;
//import ru.otus.hw.models.Author;
//import ru.otus.hw.models.Product;
//import ru.otus.hw.models.Genre;
//import ru.otus.hw.repositories.AuthorRepository;
//import ru.otus.hw.repositories.ProductRepository;
//import ru.otus.hw.repositories.GenreRepository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.springframework.util.CollectionUtils.isEmpty;
//
//@RequiredArgsConstructor
//@Service
//@Slf4j
//public class ProductServiceImpl implements ProductService {
//    private final AuthorRepository authorRepository;
//
//    private final GenreRepository genreRepository;
//
//    private final ProductRepository productRepository;
//
//    private final ProductMapper productMapper;
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public Mono<Product> findById(String id) {
//        return productRepository.findById(id);
//    }
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public Flux<Product> findAll() {
//        return productRepository.findAll();
//    }
//
//
//    @LogCustom
//    @Override
//    @Transactional
//    public Mono<Product> insert(String title, String authorId, Set<String> genresIds) {
//        return save(new, title, authorId, genresIds);
//    }
//
//    @LogCustom
//    @Override
//    @Transactional
//    public Mono<Product> update(String id, String title, String authorId, Set<String> genresIds) {
//        return save(id, title, authorId, genresIds);
//    }
//
//    @LogCustom
//    @Override
//    @Transactional
//    public Mono<Void> deleteById(String id) {
//        productRepository.deleteById(id);
//    }
//
//    private Mono<Product> save(String id, String title, String authorId, Set<String> genresIds) {
//        if (isEmpty(genresIds)) {
//            throw new IllegalArgumentException("Genres ids must not be null");
//        }
//
//        var author = authorRepository.findById(authorId)
//                .doOnError(error -> {
//                    throw new EntityNotFoundException("author with id %s not found".formatted(authorId));
//                });
//
//        var genres = genreRepository.findAllByIdIn(genresIds)
//                .doOnError(error -> {
//                    throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
//                });
//
//        var product = new Product(id, title, author.block(), genres.collectList().block());
//
//        return productRepository.save(product);
//    }
//
//}
