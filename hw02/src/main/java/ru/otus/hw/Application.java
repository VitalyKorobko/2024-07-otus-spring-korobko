package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw.service.TestRunnerService;

public class Application {
    private static final String ROOTPACKAGE = Application.class.getPackageName();

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(
                ROOTPACKAGE + ".service",
                ROOTPACKAGE + ".dao",
                ROOTPACKAGE + ".config"
        );
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}