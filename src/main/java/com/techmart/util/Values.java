package com.techmart.util;

import java.util.regex.Pattern;

public class Values {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static final long JWT_EXPIRATION_TIME = 864_000_000; // 24 hrs

}
