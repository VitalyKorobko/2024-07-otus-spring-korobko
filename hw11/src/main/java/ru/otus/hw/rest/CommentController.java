package ru.otus.hw.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;


@RestController
public class CommentController {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    public CommentController(CommentRepository commentRepository,
                             BookRepository bookRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.commentMapper = commentMapper;
    }

    @GetMapping(value = "/api/v1/comment")
    public Flux<CommentDto> getCommentsByBook(@RequestParam("book_id") String bookId) {
        return commentRepository.findByBookId(bookId).map(commentMapper::toCommentDto);
    }

    @GetMapping("/api/v1/comment/{id}")
    public Mono<CommentDto> getComment(@PathVariable("id") String id) {
        return commentRepository.findById(id).map(commentMapper::toCommentDto);
    }

    @PatchMapping("/api/v1/comment/{id}")
    public Mono<CommentDtoWeb> updateComment(@Valid @RequestBody Mono<CommentDtoWeb> monoCommentDtoWeb,
                                             @PathVariable("id") String id) {
        return getMono(monoCommentDtoWeb, id);
    }

    @PostMapping("/api/v1/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CommentDtoWeb> createComment(@Valid @RequestBody Mono<CommentDtoWeb> monoCommentDtoWeb) {
        return getMono(monoCommentDtoWeb, null);
    }

    @DeleteMapping("/api/v1/comment/{id}")
    public Mono<Void> deleteComment(@PathVariable String id) {
        return commentRepository.deleteById(id);
    }

    private Mono<CommentDtoWeb> getMono(Mono<CommentDtoWeb> monoCommentDtoWeb, String id) {
        return monoCommentDtoWeb
                .zipWhen(commentDtoWeb -> bookRepository.findById(commentDtoWeb.getBookId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("book with id = %s not found"
                                        .formatted(commentDtoWeb.getBookId()))
                                )
                        )
                )
                .flatMap(tuple ->
                        commentRepository.save(new Comment(id,
                                        tuple.getT1().getText(),
                                        tuple.getT2()
                                )
                        )
                ).map(comment -> commentMapper.commentDtoWeb(comment, null))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(new CommentDtoWeb(
                                        ex.getFieldValue("id").toString(),
                                        ex.getFieldValue("text").toString(),
                                        ex.getFieldValue("bookId").toString(),
                                        ex.getFieldError().getDefaultMessage()
                                )
                        )
                );
    }


}
