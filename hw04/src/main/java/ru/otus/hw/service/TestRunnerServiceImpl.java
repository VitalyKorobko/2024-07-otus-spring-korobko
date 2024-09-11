package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.security.LoginContext;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final LoginContext loginContext;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    private final TestValidatorService testValidatorService;

    @Override
    public void run() {
        try {
            testValidatorService.validate();
        } catch (QuestionReadException e) {
            ioService.printLine(e.getMessage());
            System.exit(0);
        } catch (RuntimeException e) {
            ioService.printLine(e.getCause().getMessage());
            System.exit(0);
        }
        var student = loginContext.get();
        try {
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException e) {
            ioService.printLineLocalized("TestRunnerService.failed.load.questions");
        } catch (IllegalArgumentException e) {
            ioService.printLineLocalized("TestRunnerService.maximum.number.attempts");
            if (ioService.readStringWithPromptLocalized("TestRunnerService.test.again.message")
                    .equalsIgnoreCase(ioService.getMessage("TestRunnerService.test.again.answer.value"))) {
                run();
            }
        }
    }

}
