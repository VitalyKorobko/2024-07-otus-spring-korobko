package ru.otus.hw.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@DisplayName("Интеграционный тест класса читающего вопросы")
@SpringBootTest
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao questionDao;

    @Configuration
    @Import(CsvQuestionDao.class)
    static class Config{

    }

    @Test
    @DisplayName("Ожидаем, что не будет выброшено исключение, если файл пуст")
    public void expectThatNoExceptionWillBeThrownIfFileIsEmpty() {
        when(fileNameProvider.getTestFileName()).thenReturn("emptyFile.csv");
        assertDoesNotThrow(() -> questionDao.findAll());
    }

    @Test
    @DisplayName("Ожидаем, что не будет выброшено исключение, если содержаться вопросы только с вариантами ответов")
    public void expectThatNoExceptionWillBeThrownIfTheQuestionsContainOnlyMultipleChoiceQuestions() {
        when(fileNameProvider.getTestFileName()).thenReturn("withMultipleChoice.csv");
        assertDoesNotThrow(() -> questionDao.findAll());
    }

    @Test
    @DisplayName("Ожидаем, что не будет выброшено исключение, если присутствует вопрос в свободной форме")
    public void expectThatNoExceptionWillBeThrownIfThereIsFreeFormQuestion() {
        when(fileNameProvider.getTestFileName()).thenReturn("includeFreeForm.csv");
        assertDoesNotThrow(() -> questionDao.findAll());
    }

    @Test
    @DisplayName("Ожидаем, что будет выброшено исключение RuntimeException, если присутствует некорректный ответ")
    public void expectExceptionToBeThrownIfInvalidResponseIsPresent() {
        when(fileNameProvider.getTestFileName()).thenReturn("invalidAnswer.csv");
        assertThrows(RuntimeException.class, () -> questionDao.findAll());
    }

    @Test
    @DisplayName("Ожидаем, что полученный список вопросов соответствует ожидаемому")
    public void expectThatReceivedListOfQuestionsIsEqualsToExpected() {
        var question_1 = new Question("What?",
                List.of(
                        new Answer("A", true),
                        new Answer("B", false),
                        new Answer("C", false)
                )
        );
        var question_2 = new Question("Why?",
                List.of(
                        new Answer("A", false),
                        new Answer("B", false),
                        new Answer("C", true)
                )
        );
        var list = List.of(question_1, question_2);
        when(fileNameProvider.getTestFileName()).thenReturn("simpleQuestions.csv");
        Assertions.assertThat(questionDao.findAll()).isEqualTo(list);
    }

    @Test
    @DisplayName("Ожидаем, что, если ресурса не существует, то будет брошено исключение QuestionReadException")
    public void checkIfResourceNotExistWillBeThrownException() {
        when(fileNameProvider.getTestFileName()).thenReturn("question.csv");
        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }


}
