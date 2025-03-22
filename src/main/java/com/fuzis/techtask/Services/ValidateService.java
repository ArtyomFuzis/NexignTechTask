package com.fuzis.techtask.Services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Сервис для валидации данных. На текущий момент содержит лишь функцию дял валидации номера телефона
 */
@Service
public class ValidateService {
    Pattern patternPhoneNumber;

    public ValidateService() {
        patternPhoneNumber = Pattern.compile("^\\+?\\d{11}$");
    }

    /**
     * Проводит валидацию номера телефона на основе regex паттерна "^\+?\d{11}$"
     *
     * @param phoneNumber номер телефона, который нужно проверить
     * @return boolean значения - корректность пришедшего номера телефона
     */
    public boolean validatePhoneNumber(String phoneNumber) {
        return patternPhoneNumber.matcher(phoneNumber).find();
    }
}
