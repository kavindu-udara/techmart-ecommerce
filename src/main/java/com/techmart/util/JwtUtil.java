package com.techmart.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    public static String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Values.JWT_EXPIRATION_TIME))
                .signWith(Values.JWT_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String validateTokenAndGetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Values.JWT_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // Returns the userId as a String
    }

}
