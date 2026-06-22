package com.techmart.util;

public class Validators {

    public static boolean isValidEmail(String email){
        if(email == null){
            return false;
        }
        return Values.EMAIL_PATTERN.matcher(email).matches();
    }

}
