//package ru.otus.hw.services;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import reactor.core.publisher.Flux;
//import ru.otus.hw.dto.AuthorDto;
//import ru.otus.hw.logging.LogCustom;
//import ru.otus.hw.mapper.AuthorMapper;
//import ru.otus.hw.models.Author;
//import ru.otus.hw.repositories.AuthorRepository;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class AuthorServiceImpl implements AuthorService {
//    private final AuthorRepository authorRepository;
//
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public Flux<Author> findAll() {
//        return authorRepository.findAll();
//    }
//
//}
