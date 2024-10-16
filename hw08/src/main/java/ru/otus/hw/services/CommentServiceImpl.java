package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.enums.Seq;
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

    private final IdSequencesService idSequencesService;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .map(commentMapper::toCommentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllCommentsByBookId(String bookId) {
        List<Comment> comments = commentRepository.findByBookId(bookId);
        return commentRepository.findByBookId(bookId).stream()
                .map(commentMapper::toCommentDto).toList();
    }

    @Override
    @Transactional
    public CommentDto insert(String text, String bookId) {
        return save(idSequencesService.getNextId(Seq.COMMENT.getSeqName()), text, bookId);
    }

    @Override
    @Transactional
    public CommentDto update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    private CommentDto save(String id, String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        return commentMapper.toCommentDto(
                commentRepository.save(new Comment(id, text, book))
        );
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }


}
