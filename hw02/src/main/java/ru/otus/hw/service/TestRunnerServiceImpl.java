package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestValidatorService testValidatorService;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final IOService ioService;

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

        var student = studentService.determineCurrentStudent();

        try {
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (IllegalArgumentException e) {
            ioService.printLine("You have exceeded the maximum number of attempts to enter an answer.");
            if (ioService.readStringWithPrompt("Try to take the test again. YES/NO?").equalsIgnoreCase("YES")) {
                run();
            }
        }
    }

}