package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;
import ru.otus.hw.security.LoginContext;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final LoginContext loginContext;

    private final LocalizedIOService ioService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");
        loginContext.regStudent(firstName, lastName);
        return loginContext.get();
    }

}
