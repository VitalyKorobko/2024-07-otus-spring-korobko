package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final IOService ioService;

    @Override
    public void run() {
        var student = studentService.determineCurrentStudent();
        try {
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException e) {
            ioService.printLine(e.getMessage());
        } catch (IllegalArgumentException e) {
            ioService.printLine("You have exceeded the maximum number of attempts to enter an answer.");
            if (ioService.readStringWithPrompt("Try to take the test again. YES/NO?").equalsIgnoreCase("YES")) {
                run();
            }
        }
    }

}