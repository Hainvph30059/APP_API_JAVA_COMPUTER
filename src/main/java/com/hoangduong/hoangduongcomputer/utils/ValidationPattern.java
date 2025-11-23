package com.hoangduong.hoangduongcomputer.utils;

public class ValidationPattern {

    public static final String FIELD_REQUIRED_MESSAGE = "This field is required.";

    public static final String EMAIL_RULE = "^\\S+@\\S+\\.\\S+$";
    public static final String EMAIL_RULE_MESSAGE = "Email is invalid. (example@trungquandev.com)";

    public static final String PASSWORD_RULE = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d\\W]{8,256}$";
    public static final String PASSWORD_RULE_MESSAGE = "Password must include at least 1 letter, a number, and at least 8 characters.";

    public static final String PASSWORD_CONFIRMATION_MESSAGE = "Password Confirmation does not match!";

    private ValidationPattern() {
    }
}
