package ru.otus.hw.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String FREE_ANSWER = "TestService.free.answer";

    private static final String SELECT_ANSWER = "TestService.select.answer";

    private static final String ENTER_VALUE = "TestService.enter.error";

    private static final String LINE_BREAK = "%n";

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            printQuestion(question);
            if (isAnswersExist(question)) {
                testWithAnswers(question, question.answers().size(), testResult);
            } else {
                testWithoutAnswers(question, testResult, String::isBlank);
            }
        }
        return testResult;
    }

    private void testWithoutAnswers(Question question, TestResult testResult, Predicate<String> predicate) {
        String freeAnswer = ioService.readStringWithPromptLocalized(FREE_ANSWER);
        if (!predicate.test(freeAnswer)) {
            testResult.applyAnswer(question, true);
        }
    }

    private void testWithAnswers(Question question, int countAnswers, TestResult testResult) {
        int answerNum = ioService.readIntForRangeWithPromptLocalized(
                1,
                countAnswers,
                SELECT_ANSWER,
                ENTER_VALUE
        );
        boolean result = question.answers().get(--answerNum).isCorrect();
        testResult.applyAnswer(question, result);


    }

    private boolean isAnswersExist(Question question) {
        return !CollectionUtils.isEmpty(question.answers());
    }

    private void printQuestion(@NonNull Question q) {
        if (isAnswersExist(q)) {
            ioService.printFormattedLine(LINE_BREAK + q.text() + LINE_BREAK + q.answers()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(a -> "-   %s".formatted(a.text()))
                    .collect(Collectors.joining(LINE_BREAK))
                    + LINE_BREAK
            );
        } else {
            ioService.printFormattedLine(LINE_BREAK + q.text() + LINE_BREAK);
        }
    }

}