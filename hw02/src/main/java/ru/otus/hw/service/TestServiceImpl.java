package ru.otus.hw.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private static final String FREE_ANSWER = "Enter your answer in free form";

    private static final String SELECT_ANSWER = "Select an answer option:";

    private static final String ENTER_VALUE = "You must enter a value from ";

    private static final String TO = " to ";

    private static final String LINE_BREAK = "%n";

    private final IOService ioService;

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
        String freeAnswer = ioService.readStringWithPrompt(FREE_ANSWER);
        if (!predicate.test(freeAnswer)) {
            testResult.applyAnswer(question, true);
        }
    }

    private void testWithAnswers(Question question, int countAnswers, TestResult testResult) {
        int answerNum = ioService.readIntForRangeWithPrompt(
                1,
                countAnswers,
                SELECT_ANSWER,
                ENTER_VALUE + 1 + TO + countAnswers
        );
        int expectedNum = getExpectedNumOfAnswer(question);
        testResult.applyAnswer(question, answerNum == expectedNum);

    }

    private int getExpectedNumOfAnswer(Question question) {
        var answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                return ++i;
            }
        }
        throw new QuestionReadException("the correct option is missing");
    }

    private boolean isAnswersExist(Question question) {
        return Objects.nonNull(
                question.answers())
                && question.answers().size() != 0
                && Objects.nonNull(question.answers().get(0)
        );
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