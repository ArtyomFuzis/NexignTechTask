package com.fuzis.techtask.Services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidateService
{
    Pattern patternPhoneNumber;

    public ValidateService(){
        patternPhoneNumber = Pattern.compile("^\\+?\\d{11}$");
    }
    public boolean validatePhoneNumber(String phoneNumber){
        return patternPhoneNumber.matcher(phoneNumber).find();
    }
}
