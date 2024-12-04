package ru.otus.hw.controller;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.converters.BookDtoConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;


@Controller
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    private final BookDtoConverter converter;

    public CommentController(CommentService commentService, BookService bookService, BookDtoConverter converter) {
        this.commentService = commentService;
        this.bookService = bookService;
        this.converter = converter;
    }

    @GetMapping("/all")
    public String getBookWithComments(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books-by-comments";
    }

    @GetMapping("/book")
    public String getCommentsByBook(Model model, @RequestParam("book_id") long bookId) {
        model.addAttribute("comments", commentService.findAllCommentsByBookId(bookId));
        model.addAttribute("bookInfo", converter.bookDtoToString(bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("такой книги не найдено"))));
        return "comments";
    }

    @GetMapping("/{id}")
    public String editComment(Model model, @PathVariable("id") long id) {
        model.addAttribute("comment", commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id = %d не найден".formatted(id))));
        model.addAttribute("books", bookService.findAll());
        return "update-comment";
    }

    @PostMapping("/{id}/update")
    public String updateComment(@Valid @ModelAttribute("comment") CommentDto comment,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comment", comment);
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("error", true);
            model.addAttribute("message", bindingResult.getFieldError().getDefaultMessage());
            return "update-comment";
        }
        commentService.update(comment.getId(), comment.getText(), comment.getBookId());
        return "redirect:/comment/book?book_id=%d".formatted(comment.getBookId());
    }

    @GetMapping("/new")
    public String addComment(Model model) {
        model.addAttribute("comment", new CommentDto());
        model.addAttribute("books", bookService.findAll());
        return "add-comment";
    }

    @PostMapping("/new")
    public String saveComment(@Valid @ModelAttribute("comment") CommentDto comment,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("comment", comment);
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("error", true);
            model.addAttribute("message", bindingResult.getFieldError().getDefaultMessage());
            return "add-comment";
        }
        commentService.insert(comment.getText(), comment.getBookId());
        return "redirect:/comment/book?book_id=%d".formatted(comment.getBookId());
    }

    @PostMapping("/del/{id}")
    public String deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
        return "redirect:/comment/all";
    }


}
