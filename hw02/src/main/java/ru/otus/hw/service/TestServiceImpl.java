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
    private static final String FREEANSWER = "Enter your answer in free form";
    private static final String SELECTANSWER = "Select an answer option:";
    private static final String ENTERVALUE = "You must enter a value from ";
    private static final String TO = " to ";
    private static final String LINEBREAK = "%n";
    private final IOService ioService;
    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
//        ioService.printLine("");
//        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            // Задать вопрос, получить ответ
            printQuestion(question);
            if (isAnswersExist(question)) {
                testWithAnswers(question, questions.size(), testResult);
            } else {
                testWithoutAnswers(question, testResult, String::isBlank);
            }
        }
        return testResult;
    }

    private void testWithoutAnswers(Question question, TestResult testResult, Predicate<String> predicate) {
        String freeAnswer = ioService.readStringWithPrompt(FREEANSWER);
        if (!predicate.test(freeAnswer)) {
            testResult.applyAnswer(question, true);
        }
    }

    private void testWithAnswers(Question question, int countAnswers, TestResult testResult) {
        int answerNum = ioService.readIntForRangeWithPrompt(
                1,
                countAnswers,
                SELECTANSWER,
                ENTERVALUE + 1 + TO + countAnswers
        );
        int expectedNum = getExpectedNumOfAnswer(question);
        testResult.applyAnswer(question, answerNum == expectedNum);

    }

    private int getExpectedNumOfAnswer(Question question) {
        var answers = question.answers();
        checkCountOfTrueAnswers(question);
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                return ++i;
            }
        }
        throw new QuestionReadException("the correct option is missing");
    }

    private void checkCountOfTrueAnswers(Question question) {
        if (question.answers()
                .stream()
                .map(a -> a.isCorrect())
                .filter((el) -> el)
                .count() >= 2) {
            throw new QuestionReadException("more than one correct answer");
        }
    }

    private boolean isAnswersExist(Question question) {
        return Objects.nonNull(question.answers()) && question.answers().size() != 0 && Objects.nonNull(question.answers().get(0));
    }

    private void printQuestion(@NonNull Question q) {
        if (isAnswersExist(q)) {
            ioService.printFormattedLine(LINEBREAK + q.text() + LINEBREAK + q.answers()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(a -> "-   %s".formatted(a.text()))
                    .collect(Collectors.joining(LINEBREAK))
                    + LINEBREAK
            );
        } else {
            ioService.printFormattedLine(LINEBREAK + q.text() + LINEBREAK);
        }

    }

//    private void printQuestion(Question q) {
//        if (Objects.nonNull(q.answers())) {
//            ioService.printFormattedLine(q.text() + LINEBREAK + q.answers()
//                    .stream()
//                    .filter(Objects::nonNull)
//                    .map(a -> "-   %s".formatted(a.text()))
//                    .collect(Collectors.joining(LINEBREAK))
//                    + LINEBREAK
//            );
//        } else {
//            ioService.printFormattedLine(q.text() + LINEBREAK);
//        }
//
//    }

}