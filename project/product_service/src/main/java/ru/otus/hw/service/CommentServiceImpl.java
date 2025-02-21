//package ru.otus.hw.services;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.otus.hw.dto.CommentDto;
//import ru.otus.hw.exceptions.EntityNotFoundException;
//import ru.otus.hw.logging.LogCustom;
//import ru.otus.hw.mapper.CommentMapper;
//import ru.otus.hw.models.Comment;
//import ru.otus.hw.repositories.ProductRepository;
//import ru.otus.hw.repositories.CommentRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class CommentServiceImpl implements CommentService {
//    private final CommentRepository commentRepository;
//
//    private final ProductRepository productRepository;
//
//    private final CommentMapper commentMapper;
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<CommentDto> findById(long id) {
//        return commentRepository.findById(id)
//                .map(commentMapper::toCommentDto);
//    }
//
//    @LogCustom
//    @Override
//    @Transactional(readOnly = true)
//    public List<CommentDto> findAllCommentsByProductId(long productId) {
//        return commentRepository.findByProductId(productId).stream()
//                .map(commentMapper::toCommentDto).toList();
//    }
//
//    @LogCustom
//    @Override
//    @Transactional
//    public CommentDto insert(String text, long productId) {
//        return save(0L, text, productId);
//    }
//
//    @LogCustom
//    @Override
//    @Transactional
//    public CommentDto update(long id, String text, long productId) {
//        return save(id, text, productId);
//    }
//
//    private CommentDto save(long id, String text, long productId) {
//        var product = productRepository.findById(productId)
//                .orElseThrow(() -> new EntityNotFoundException("Product with id %d not found".formatted(productId)));
//        return commentMapper.toCommentDto(
//                commentRepository.save(new Comment(id, text, product))
//        );
//    }
//
//    @Override
//    @Transactional
//    public void deleteById(long id) {
//        commentRepository.deleteById(id);
//    }
//}
