package ru.otus.hw.service;

public interface RegService {
    boolean checkEmailByDuplicateEmail(String email);

    boolean checkPasswords(String password, String repeatPassword);

}
