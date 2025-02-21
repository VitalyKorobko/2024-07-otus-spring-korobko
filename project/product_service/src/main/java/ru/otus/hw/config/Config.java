package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Random;

@Configuration
public class Config {

    @Bean
    @Scope("prototype")
    public Random get(){
        return new Random(47);
    }

}
