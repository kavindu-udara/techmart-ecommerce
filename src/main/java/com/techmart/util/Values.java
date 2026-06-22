package com.techmart.util;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.regex.Pattern;

public class Values {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String JWT_SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    public static final Key JWT_KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    public static final long JWT_EXPIRATION_TIME = 864_000_000; // 24 hrs

}
