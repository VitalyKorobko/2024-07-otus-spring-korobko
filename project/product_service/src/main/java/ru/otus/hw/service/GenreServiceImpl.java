//package ru.otus.hw.services;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import reactor.core.publisher.Flux;
//import ru.otus.hw.dto.GenreDto;
//import ru.otus.hw.logging.LogCustom;
//import ru.otus.hw.mapper.GenreMapper;
//import ru.otus.hw.models.Genre;
//import ru.otus.hw.repositories.GenreRepository;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class GenreServiceImpl implements GenreService {
//    private final GenreRepository genreRepository;
//
//    private final GenreMapper genreMapper;
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public Flux<Genre> findAll() {
//        return genreRepository.findAll();
//    }
//
//}
