package ru.otus.hw.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.BookDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@Controller
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

    @GetMapping("/")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }

    @GetMapping("/book")
    public String editBook(@RequestParam("num") long id, Model model) {
        var bookDto = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id = %d не найдена".formatted(id)));
        model.addAttribute("book", mapper.toBookDtoWeb(bookDto));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "update-book";
    }

    @GetMapping("/book/new")
    public String addBook(Model model) {
        model.addAttribute("book", new BookDtoWeb());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "add-book";
    }

    @PostMapping("/book/new")
    public String saveBook(
            @Valid @ModelAttribute("book") BookDtoWeb book,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "add-book";
        }
        bookService.insert(
                book.getTitle(),
                book.getAuthorId(),
                book.getSetGenresId()
        );
        return "redirect:/";
    }

    @PostMapping("/book/{id}/update")
    public String updateBook(
            @Valid @ModelAttribute("book") BookDtoWeb book,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
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

    @GetMapping("/book/del/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }


}
