package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.logging.LogCustom;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @LogCustom
    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findById(long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookDto);
    }

    @LogCustom
    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookDto).toList();
    }


    @LogCustom
    @Override
    @Transactional
    public BookDto insert(String title, String authorFullName, Set<String> genreNames) {
        var authorId = getAuthorId(authorFullName);
        var genresIds = getGenreNames(genreNames);
        return save(0L, title, authorId, genresIds);
    }

    @LogCustom
    @Override
    @Transactional
    public BookDto update(long id, String title, String authorFullName, Set<String> genreNames) {
        var authorId = getAuthorId(authorFullName);
        var genresIds = getGenreNames(genreNames);
        return save(id, title, authorId, genresIds);
    }

    @LogCustom
    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);

        return bookMapper.toBookDto(bookRepository.save(book));
    }

    private long getAuthorId(String authorFullName) {
        var result = authorRepository.findByFullName(authorFullName);
        if (CollectionUtils.isEmpty(result)) {
            throw new EntityNotFoundException("author with FullName: %s not found".formatted(authorFullName));
        }
        return authorRepository.findByFullName(authorFullName).get(0).getId();
    }

    private Set<Long> getGenreNames(Set<String> genreNames) {
        var genres = genreRepository.findAllByNameIn(genreNames);
        return genres.stream().map(Genre::getId).collect(Collectors.toSet());
    }
}
