package ru.otus.hw.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.Map;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final BookMapper mapper;

    public BookController(BookService bookService, AuthorService authorService,
                          GenreService genreService, BookMapper mapper) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public String editBook(@PathVariable("id") long id, Model model) {
        var bookDto = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id = %d не найдена".formatted(id)));
        addAllAttributes(model, mapper.toBookDtoWeb(bookDto));
        return "update-book";
    }

    @GetMapping("/new")
    public String addBook(Model model) {
        addAllAttributes(model, new BookDtoWeb());
        return "add-book";
    }

    @PostMapping("/new")
    public String saveBook(
            @Valid @ModelAttribute("book") BookDtoWeb book,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addAllAttributes(model, book);
            model.addAllAttributes(Map.of(
                            "message", bindingResult.getFieldError().getDefaultMessage(),
                            "error", true
                    )
            );
            return "add-book";
        }
        bookService.insert(
                book.getTitle(),
                book.getAuthorId(),
                book.getSetGenresId()
        );
        return "redirect:/";
    }

    @PostMapping("/{id}/update")
    public String updateBook(
            @Valid @ModelAttribute("book") BookDtoWeb book,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addAllAttributes(model, book);
            return "update-book";
        }
        bookService.update(
                book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getSetGenresId()
        );
        return "redirect:/";
    }

    @PostMapping("/del/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    private void addAllAttributes(Model model, BookDtoWeb book) {
        addAllAttributes(model, Map.of("book", book,
                "authors", authorService.findAll(),
                "genres", genreService.findAll())
        );
    }

    private void addAllAttributes(Model model, Map<String, Object> map) {
        for (String key : map.keySet()) {
            model.addAttribute(key, map.get(key));
        }
    }


}
