package ru.otus.hw.services.gates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import ru.otus.hw.config.IntegrationConfig;
import ru.otus.hw.domain.Article;
import ru.otus.hw.domain.NewsPaper;
import ru.otus.hw.domain.Writer;
import ru.otus.hw.services.WriterService;
import ru.otus.hw.services.ArticleService;
import ru.otus.hw.services.NewsPaperService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {IntegrationConfig.class,})
@IntegrationComponentScan(basePackages = {"ru.otus.hw.services.gates"})
@EnableIntegration
@EnableIntegrationManagement
public class NewsPaperGatewayTest {
    private static final short AMOUNT_WORDS_FOR_ARTICLE = 31;

    private static final short AMOUNT_ARTICLES_IN_NEWSPAPER_FOR_SAVE = 7;

    private static final short AMOUNT_ARTICLES_IN_NEWSPAPER_FOR_NON_SAVE = 6;

    private static final String NEWSPAPER_NAME = "name";

    private static final String NEWSPAPER_ID = "1";

    private static final String WRITER_NAME = "Sidorov";

    private static final String WRITER_ID = "1";

    private static final String ARTICLE_NAME = "title";

    private static final String ARTICLE_ID = "1";

    private static final String ANY_WORD = "word";

    private static final short AMOUNT_SUCCESSFULL_METHOD_CALLS = 1;

    private static final short AMOUNT_UNSUCCESSFULL_METHOD_CALLS = 0;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private WriterService writerService;

    @MockBean
    private NewsPaperService newsPaperService;

    @Autowired
    private NewsPaperGateway newsPaperGateway;

    @Test
    @DisplayName("должна возвращаться и сохраняться ожидаемая газета при достаточном количестве статей")
    void shouldReturnAndSaveExpectedNewsPaper() {
        assertNotNull(newsPaperGateway);
        assertNotNull(newsPaperService);

        var listArticles = getArticles(AMOUNT_ARTICLES_IN_NEWSPAPER_FOR_SAVE);
        var newsPaper = new NewsPaper(NEWSPAPER_ID, NEWSPAPER_NAME, listArticles);
        var expectedNewsPaper = new NewsPaper(NEWSPAPER_ID, NEWSPAPER_NAME.toUpperCase(), listArticles);

        given(newsPaperService.get(any())).willReturn(newsPaper);
        var result = newsPaperGateway.create(listArticles);

        assertEquals(expectedNewsPaper, result);
        verify(newsPaperService, times(AMOUNT_SUCCESSFULL_METHOD_CALLS)).save(result);
    }

    @Test
    @DisplayName("не должна сохраняться газета при не достаточном количестве статей")
    void shouldNotSaveNewsPaper() {
        assertNotNull(newsPaperGateway);
        assertNotNull(newsPaperService);

        var listArticles = getArticles(AMOUNT_ARTICLES_IN_NEWSPAPER_FOR_NON_SAVE);
        var newsPaper = new NewsPaper(NEWSPAPER_ID, NEWSPAPER_NAME, listArticles);

        given(newsPaperService.get(any())).willReturn(newsPaper);
        var result = newsPaperGateway.create(listArticles);

        verify(newsPaperService, times(AMOUNT_UNSUCCESSFULL_METHOD_CALLS)).save(result);
    }

    private List<Article> getArticles(int amount) {
        var writer = new Writer(WRITER_ID, WRITER_NAME, null);
        var article = new Article(
                ARTICLE_ID,
                ARTICLE_NAME,
                writer,
                getWordsList(AMOUNT_WORDS_FOR_ARTICLE)
        );
        return IntStream.range(0, amount).mapToObj(num -> article).collect(Collectors.toList());
    }

    private List<String> getWordsList(int count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(ANY_WORD + i);
        }
        return list;
    }

}
