package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final String LINEBREAK = "%n";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        questions.forEach(this::printQuestion);
    }

    private void printQuestion(Question q) {
        if (Objects.nonNull(q.answers())) {
            ioService.printFormattedLine(q.text() + LINEBREAK + q.answers()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(a -> "-   %s".formatted(a.text()))
                    .collect(Collectors.joining(LINEBREAK))
                    + LINEBREAK
            );
        } else {
            ioService.printFormattedLine(q.text() + LINEBREAK);
        }

    }
}
