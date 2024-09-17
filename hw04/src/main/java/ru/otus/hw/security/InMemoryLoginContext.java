package ru.otus.hw.security;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Student;

import java.util.Objects;

@Component
public class InMemoryLoginContext implements LoginContext {

    private Student student;

    @Override
    public boolean isStudentLoggedIn() {
        return Objects.nonNull(student);
    }

    @Override
    public void authorizeStudent(String firstname, String lastname) {
        student = new Student(firstname, lastname);
    }

    @Override
    public Student get() {
        return student;
    }
}
