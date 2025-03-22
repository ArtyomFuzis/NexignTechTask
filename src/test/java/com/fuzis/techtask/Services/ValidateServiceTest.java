package com.fuzis.techtask.Services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса ValidateService
 */
class ValidateServiceTest {
    /**
     * Тест на работоспособность метода ValidatePhoneNumber
     */
    @Test
    void testValidatePhoneNumber() {
        ValidateService vs = new ValidateService();
        String testPhoneNumber1 = "+75556665458";
        String testPhoneNumber2 = "+755566654585";
        String testPhoneNumber3 = "855566654585";
        String testPhoneNumber4 = "85566654585";
        String testPhoneNumber5 = "855666a4585";
        assert vs.validatePhoneNumber(testPhoneNumber1);
        assert !vs.validatePhoneNumber(testPhoneNumber2);
        assert !vs.validatePhoneNumber(testPhoneNumber3);
        assert vs.validatePhoneNumber(testPhoneNumber4);
        assert !vs.validatePhoneNumber(testPhoneNumber5);
    }
}