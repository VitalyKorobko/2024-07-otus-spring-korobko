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
import ru.otus.hw.domain.Writer;
import ru.otus.hw.services.WriterService;
import ru.otus.hw.services.ArticleService;
import ru.otus.hw.services.NewsPaperService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {IntegrationConfig.class})
@IntegrationComponentScan(basePackages = {"ru.otus.hw.services.gates"})
@EnableIntegration
@EnableIntegrationManagement
public class ArticleGatewayTest {
    private static final short AMOUNT_WORDS_FOR_GOOD_ARTICLE = 31;

    private static final short AMOUNT_WORDS_FOR_BAD_ARTICLE = 29;

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
    private ArticleGateway articleGateway;


    @Test
    @DisplayName("должна возвращаться и сохраняться ожидаемая статья при прохождении фильтра")
    void shouldReturnAndSaveExpectedArticle() {
        assertNotNull(articleGateway);
        assertNotNull(writerService);
        assertNotNull(articleService);

        var writer = getWriter();
        var expectedArticle = getArticle(writer, AMOUNT_WORDS_FOR_GOOD_ARTICLE);

        given(writerService.create(any())).willReturn(writer);
        given(articleService.create(any())).willReturn(expectedArticle);

        var result = articleGateway.create(WRITER_NAME);

        assertEquals(expectedArticle, result);
        verify(articleService, times(AMOUNT_SUCCESSFULL_METHOD_CALLS)).save(result);
    }

    @Test
    @DisplayName("не должна сохраняться статья не прощедшая фильтр")
    void shouldNotSaveBadArticle() {
        var writer = getWriter();
        var expectedArticle = getArticle(writer, AMOUNT_WORDS_FOR_BAD_ARTICLE);

        given(writerService.create(any())).willReturn(writer);
        given(articleService.create(any())).willReturn(expectedArticle);

        var result = articleGateway.create(WRITER_NAME);
        verify(articleService, times(AMOUNT_UNSUCCESSFULL_METHOD_CALLS)).save(result);
    }

    private Article getArticle(Writer writer, short amountWords) {
        return new Article(
                ARTICLE_ID,
                ARTICLE_NAME,
                writer,
                getWordsList(amountWords)
        );
    }

    private Writer getWriter() {
        return new Writer(WRITER_ID, WRITER_NAME, null);
    }


    private List<String> getWordsList(int count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(ANY_WORD + i);
        }
        return list;
    }


}
