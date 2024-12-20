package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static java.lang.System.out;

@SpringBootApplication
//@EnableAspectJAutoProxy
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);

        out.println("http://localhost:8080/monitor/metrics");
        out.println("http://localhost:8080/monitor/health");
        out.println("http://localhost:8080/monitor/health/bookIndicator");
        out.println("http://localhost:8080/monitor/logfile");
        out.println("http://localhost:8080/datarest");
        out.println("http://localhost:8080");
        out.println("http://localhost:9000");



    }

}
