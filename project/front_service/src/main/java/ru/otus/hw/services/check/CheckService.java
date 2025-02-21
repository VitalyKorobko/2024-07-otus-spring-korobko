package ru.otus.hw.services.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckService {
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
    public boolean checkUsernameByDuplicate(String username, long user_id) {
        return userRepository.findByUsername(username).isEmpty() ||
                userRepository.findByUsername(username).get().getId() == user_id;
    }

    //проверка, что второго такого e-mail не существует в бд при редактировании пользователя
    public boolean checkEmailByDuplicate(String email, long user_id) {
        return userRepository.findByEmail(email).isEmpty() ||
                userRepository.findByEmail(email).get().getId() == user_id;
    }

    public boolean checkValue(String number) {
        List<Character> list = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        int count = 0;
        for (Character ch : number.toCharArray()) {
            if (list.contains(ch)) {
                count++;
            }
        }
        return count == number.length();
    }
}
