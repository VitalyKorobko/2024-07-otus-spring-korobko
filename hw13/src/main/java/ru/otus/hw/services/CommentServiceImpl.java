package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toCommentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllCommentsByBookId(long bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(commentMapper::toCommentDto).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'USER')")
    public CommentDto insert(String text, long bookId) {
        return save(0L, text, bookId);
    }

    @Override
    @Transactional
    //todo Добавить: Пользователь может редактровать свои комментарии
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public CommentDto update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    private CommentDto save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        return commentMapper.toCommentDto(
                commentRepository.save(new Comment(id, text, book))
        );
    }


    @Override
    @Transactional
    //todo ДОбвавить: Пользователь может удалять свои комментарии
    @PreAuthorize("hasAnyRole('ADMIN'")
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
