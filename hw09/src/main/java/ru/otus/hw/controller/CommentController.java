package ru.otus.hw.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.converters.BookDtoConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

@Controller
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    private final BookDtoConverter converter;

    public CommentController(CommentService commentService, BookService bookService, BookDtoConverter converter) {
        this.commentService = commentService;
        this.bookService = bookService;
        this.converter = converter;
    }

    @GetMapping("comments")
    public String listBookWithComments(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books-by-comments";
    }

    @GetMapping("/comments/{id}")
    public String listCommentsByBook(Model model, @PathVariable("id") long id) {
        model.addAttribute("comments", commentService.findAllCommentsByBookId(id));
        model.addAttribute("bookInfo", converter.bookDtoToString(bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("такой книги не найдено"))));
        return "comments";
    }

    @GetMapping("/comment")
    public String editComment(Model model, @RequestParam("num") long id) {
        model.addAttribute("comment", commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id = %d не найден".formatted(id))));
        model.addAttribute("books", bookService.findAll());
        return "update-comment";
    }

    @PostMapping("/comment")
    public String updateComment(@Valid @ModelAttribute("comment") CommentDto comment,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comment", comment);
            model.addAttribute("books", bookService.findAll());
            return "update-comment";
        }
        commentService.update(comment.getId(), comment.getText(), comment.getBookId());
        return "redirect:/comments/%d".formatted(comment.getBookId());
    }

    @GetMapping("/comment/new")
    public String addComment(Model model) {
        model.addAttribute("comment", new CommentDto());
        model.addAttribute("books", bookService.findAll());
        return "add-comment";
    }

    @PostMapping("/comment/new")
    public String saveComment(@Valid @ModelAttribute("comment") CommentDto comment,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comment", comment);
            model.addAttribute("books", bookService.findAll());
            return "add-comment";
        }
        commentService.insert(comment.getText(), comment.getBookId());
        return "redirect:/comments/%d".formatted(comment.getBookId());
    }

    @GetMapping("/comment/del/{id}")
    public String deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
        return "redirect:/comments";
    }


}
