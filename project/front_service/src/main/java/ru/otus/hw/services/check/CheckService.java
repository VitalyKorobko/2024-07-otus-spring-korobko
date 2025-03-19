package ru.otus.hw.services.check;

public interface CheckService {
    boolean checkFieldLength(String string);

    boolean checkUsernameByDuplicate(String username);

    boolean checkEmailByDuplicateEmail(String email);

    boolean checkPasswords(String password, String repeatPassword);

    boolean checkUsernameByDuplicate(String username, long userId);

    boolean checkEmailByDuplicate(String email, long userId);

    boolean checkValue(String number);

}
