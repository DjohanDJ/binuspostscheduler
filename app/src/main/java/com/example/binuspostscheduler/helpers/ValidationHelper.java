package com.example.binuspostscheduler.helpers;

import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationHelper {

    public boolean doEmailValidation(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}
