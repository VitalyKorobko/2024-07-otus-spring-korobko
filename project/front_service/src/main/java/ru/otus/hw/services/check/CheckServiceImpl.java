package ru.otus.hw.services.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {
    private final UserRepository userRepository;

    //проверка длины строки в поле ввода, для полей varchar(255) в бд
    public boolean checkFieldLength(String string) {
        return string.length() < 255;
    }

    //проверка, что второго такого email нет в бд
    public boolean checkUsernameByDuplicate(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    //проверка, что второго такого email не существует в БД
    public boolean checkEmailByDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean checkPasswords(String password, String repeatPassword) {
        return password.equals(repeatPassword);
    }

    //проверка, что второго такого логина не существует в бд при редактировании пользователя
    public boolean checkUsernameByDuplicate(String username, long userId) {
        return userRepository.findByUsername(username).isEmpty() ||
                userRepository.findByUsername(username).get().getId() == userId;
    }

    //проверка, что второго такого e-mail не существует в бд при редактировании пользователя
    public boolean checkEmailByDuplicate(String email, long userId) {
        return userRepository.findByEmail(email).isEmpty() ||
                userRepository.findByEmail(email).get().getId() == userId;
    }

    public boolean checkValue(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
