package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@DisplayName("Сервис тестирования TestServiceImpl")
@SpringBootTest
public class TestServiceImplTest {
    private static final String FREE_ANSWER = "TestService.free.answer";

    private static final String SELECT_ANSWER = "TestService.select.answer";

    private static final String ENTER_VALUE = "TestService.enter.error";

    @MockBean
    private LocalizedIOService ioService;

    @MockBean
    private CsvQuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Configuration
    @Import(TestServiceImpl.class)
    static class Config{

    }

    @Test
    @DisplayName("Проверяем, что возвращаемый результат ответов на вопросы соответствует ожидаемому (вопросы с вариантами ответов)")
    public void testExecuteTestForWhereQuestionsWithAnswers() throws Exception {
        Question question_1 = new Question("what is it?",
                List.of(
                        new Answer("A", true),
                        new Answer("B", false)
                )
        );
        Question question_2 = new Question("what is it?",
                List.of(
                        new Answer("A", true),
                        new Answer("B", false)
                )
        );
        List<Question> questions = List.of(question_1, question_2);

        Student student = new Student("Ivan", "Ivanov");

        TestResult testResult = new TestResult(student);
        testResult.applyAnswer(question_1, true);
        testResult.applyAnswer(question_2, true);

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeWithPromptLocalized(
                1,
                2,
                SELECT_ANSWER,
                ENTER_VALUE)
        ).thenReturn(1);
        assertThat(testService.executeTestFor(student)).isEqualTo(testResult);
    }

    @Test
    @DisplayName("Проверяем, что если хоть какой-то ответ в свободной форме есть, то считаем, что он правильный")
    public void testExecuteTestForWhereQuestionsWithoutAnswers() {
        Question question = new Question("What is it?", null);
        List<Question> questions = List.of(question);

        Student student = new Student("Ivan", "Ivanov");

        TestResult testResult = new TestResult(student);
        testResult.applyAnswer(question, true);

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readStringWithPromptLocalized(FREE_ANSWER)).thenReturn("some answer");

        assertThat(testService.executeTestFor(student)).isEqualTo(testResult);
    }

}

