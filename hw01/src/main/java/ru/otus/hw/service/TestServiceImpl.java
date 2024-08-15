package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        var questions = questionDao.findAll();
        questions.forEach(q -> ioService.printFormattedLine(q.text() + "%n" + q.answers()
                        .stream()
                        .map(Answer::text)
                        .collect(StringBuilder::new,
                                (sb, s) -> sb.append("    - ").append(s).append("\n"),
                                StringBuilder::append
                        )
                )
        );
    }
}
