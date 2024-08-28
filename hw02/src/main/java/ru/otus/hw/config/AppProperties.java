package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "application.properties", factory = DefaultPropertySourceFactory.class)
public class AppProperties implements TestConfig, TestFileNameProvider {

    @Value("${test.fileName}")
    private String testFileName;

    @Value("${test.rightAnswersCountToPass}")
    private int rightAnswersCountToPass;

}
