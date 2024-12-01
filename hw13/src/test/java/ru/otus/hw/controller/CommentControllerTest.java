package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.converters.AuthorDtoConverter;
import ru.otus.hw.converters.BookDtoConverter;
import ru.otus.hw.converters.GenreDtoConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({CommentController.class, CommentMapper.class,
        BookDtoConverter.class, AuthorDtoConverter.class, GenreDtoConverter.class})
@Import({SecurityConfiguration.class})
public class CommentControllerTest {
    private static final String ERROR_MESSAGE_TEXT_FIELD = "Текст комментария должен быть минимум 5 символов";

    private static final String ERROR_MESSAGE_BOOK_FIELD = "Выберите книгу";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Должен возвращать страницу для редактирования комментария")
    void shouldReturnEditCommentPage() throws Exception {
        List<BookDto> bookDtoList = List.of(new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111"))
                )
        );
        CommentDto commentDto = new CommentDto(1, "comment_111", 1);
        given(bookService.findAll()).willReturn(bookDtoList);
        given(commentService.findById(1)).willReturn(Optional.of(commentDto));

        var result = mvc.perform(get("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andExpect(model().attribute("comment", commentDto))
                .andReturn().getResponse().getContentAsString();
        assertThat(result)
                .contains("title_111")
                .contains("comment_111");
    }

    @Test
    @WithMockUser
    @DisplayName("Должен возвращать страницу для добавления нового комментария")
    void shouldReturnDataByNewCommentPAge() throws Exception {
        List<BookDto> bookDtoList = List.of(new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111"))
                )
        );
        CommentDto commentDto = new CommentDto(0, null, 0);
        given(bookService.findAll()).willReturn(bookDtoList);
        given(commentService.findById(1)).willReturn(Optional.of(commentDto));

        var result = mvc.perform(get("/comment/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andExpect(model().attribute("comment", commentDto))
                .andReturn().getResponse().getContentAsString();
        assertThat(result)
                .contains("title_111");
    }

    @Test
    @WithMockUser
    @DisplayName("Должен сохранять новый комментарий и перенаправлять на страницу комментариев соответствующей книги")
    void shouldSaveNewComment() throws Exception {
        mvc.perform(post("/comment/new")
                        .param("id", "1")
                        .param("text", "comment_111")
                        .param("bookId", "1")
                )
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/comment/book?book_id=1"));
        verify(commentService, times(1)).insert("comment_111", 1);

    }

    @Test
    @WithMockUser
    @DisplayName("Не должен сохранять новый комментарий, если текст комментария меньше 5 символов")
    void shouldNotSaveNewCommentIfTextFieldSmallerThen5Chars() throws Exception {
        List<BookDto> bookDtoList = List.of(new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111"))
                )
        );
        CommentDto commentDto = new CommentDto(1, "com", 1);
        given(bookService.findAll()).willReturn(bookDtoList);

        String result = mvc.perform(post("/comment/new")
                        .param("id", "1")
                        .param("text", "com")
                        .param("bookId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("comment", commentDto))
                .andExpect(model().attribute("message", ERROR_MESSAGE_TEXT_FIELD))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("title_111")
                .contains(ERROR_MESSAGE_TEXT_FIELD);
    }

    @Test
    @WithMockUser
    @DisplayName("Не должен сохранять новый комментарий, если книга не выбрана")
    void shouldNotSaveNewCommentWhenBookIsNotSelected() throws Exception {
        List<BookDto> bookDtoList = List.of(new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111"))
                )
        );
        CommentDto commentDto = new CommentDto(1, "comment_111", 0);
        given(bookService.findAll()).willReturn(bookDtoList);

        String result = mvc.perform(post("/comment/new")
                        .param("id", "1")
                        .param("text", "comment_111")
                        .param("bookId", "0")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("comment", commentDto))
                .andExpect(model().attribute("message", ERROR_MESSAGE_BOOK_FIELD))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("title_111")
                .contains(ERROR_MESSAGE_BOOK_FIELD);
    }

    @Test
    @WithMockUser
    @DisplayName("Должен изменять существующий комментарий и перенаправлять на страницу с соотвествующей книгой")
    void shouldSaveExistComment() throws Exception {
        mvc.perform(post("/comment/1/update")
                        .param("id", "1")
                        .param("text", "comment_111")
                        .param("bookId", "1")
                )
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/comment/book?book_id=1"));
        verify(commentService, times(1)).update(1, "comment_111", 1);
    }

    @Test
    @WithMockUser
    @DisplayName("Не должен сохранять существующий комментарий, если текст комментария меньше 5 символов")
    void shouldNotSaveExistCommentIfTextFieldSmallerThen5Chars() throws Exception {
        List<BookDto> bookDtoList = List.of(new BookDto(
                        1,
                        "title_111",
                        new AuthorDto(1, "author_111"),
                        List.of(new GenreDto(1, "genre_111"))
                )
        );
        CommentDto commentDto = new CommentDto(1, "com", 1);
        given(bookService.findAll()).willReturn(bookDtoList);

        String result = mvc.perform(post("/comment/1/update")
                        .param("id", "1")
                        .param("text", "com")
                        .param("bookId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", bookDtoList))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("comment", commentDto))
                .andExpect(model().attribute("message", ERROR_MESSAGE_TEXT_FIELD))
                .andReturn().getResponse().getContentAsString();
        assertThat(result).contains("title_111")
                .contains(ERROR_MESSAGE_TEXT_FIELD);
    }

    @Test
    @WithMockUser
    @DisplayName("Должен удалять комментарий")
    void shouldDeleteComment() throws Exception {
        mvc.perform(post("/comment/del/1"))
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/comment/all"));
        verify(commentService, times(1)).deleteById(1L);
    }

}
