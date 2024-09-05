package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TestValidatorServiceImpl implements TestValidatorService {
    private final QuestionDao questionDao;

    @Override
    public void validate() {
        List<Question> questions = questionDao.findAll();
        checkQuestions(questions);
    }

    private void checkQuestions(List<Question> questions) {
        if (Objects.isNull(questions) || questions.isEmpty()) {
            throw new QuestionReadException("no questions on the test");
        }
        questions.forEach(q -> checkCountOfTrueAnswers(q));
    }

    private void checkCountOfTrueAnswers(Question question) {
        if (isAnswersExist(question)) {
            var count = question.answers()
                    .stream()
                    .filter(Answer::isCorrect)
                    .count();
            if (count > 1) {
                throw new QuestionReadException("more than one correct answer");
            } else if (count == 0) {
                throw new QuestionReadException("no one correct answer");
            }
        }
    }

    private boolean isAnswersExist(Question question) {
        return Objects.nonNull(question.answers())
                && question.answers().size() != 0
                && Objects.nonNull(question.answers().get(0)
        );
    }
}

