package ru.otus.hw.shell;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.security.LoginContext;
import ru.otus.hw.service.TestRunnerService;

import java.util.Objects;

@ShellComponent(value = "application commands")
public class ApplicationCommands {

    private final TestRunnerService testRunnerService;

    private final LoginContext loginContext;

    public ApplicationCommands(TestRunnerService testRunnerService, LoginContext loginContext) {
        this.testRunnerService = testRunnerService;
        this.loginContext = loginContext;
    }

    @ShellMethod(value = "login student command", key = {"l", "login"})
    public String loginStudent(@ShellOption() String firstname, @ShellOption String lastname) {
        if (Objects.isNull(firstname) || Objects.isNull(lastname)) {
            return ("You are entering incorrect login information.\nEnter, please, your firstname and lastname");
        }
        loginContext.authorizeStudent(firstname, lastname);
        return String.format("Hello, %s %s!\n\nEnter 'test' to start testing", firstname, lastname);
    }

    @ShellMethod(value = "test student", key = {"test", "t"})
    @ShellMethodAvailability(value = "isStudentLoggedIn")
    public void testStudent() {
        testRunnerService.run();
    }

    private Availability isStudentLoggedIn() {
        return loginContext.isStudentLoggedIn()
                ? Availability.available()
                : Availability.unavailable("You need to log in to start testing.");
    }

}
