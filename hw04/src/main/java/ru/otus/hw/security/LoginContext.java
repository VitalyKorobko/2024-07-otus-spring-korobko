package ru.otus.hw.security;

import ru.otus.hw.domain.Student;

import java.util.function.Supplier;

public interface LoginContext extends Supplier<Student> {
    boolean isStudentLoggedIn();

    void authorizeStudent(String firstname, String lastname);
}
