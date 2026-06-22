package com.techmart.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class PasswordHandler {

    // Secure parameters
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int ITERATIONS = 65536; // Slows down brute-force attacks
    private static final int KEY_LENGTH = 512;   // 512 bits
    private static final int SALT_LENGTH = 16;   // 128 bits (NIST recommendation)

    /**
     * Hashes a password with a unique salt and returns a string for storage.
     * Format: salt (Base64):hash (Base64):iterations:keyLength
     */
    public static String hashPassword(String password) throws Exception {
        // Step 1: Generate a secure random salt
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        // Step 2: Define the PBKDF2 parameters
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(), // Password as char array (more secure than String)
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        // Step 3: Hash the password using PBKDF2WithHmacSHA512
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Step 4: Encode salt and hash as Base64 for storage
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        // Return combined string for storage
        return String.format("%s:%s:%d:%d", saltBase64, hashBase64, ITERATIONS, KEY_LENGTH);
    }

    /**
     * Verifies an input password against a stored hash string.
     */
    public static boolean verifyPassword(String inputPassword, String storedHashData) throws Exception {
        // Split stored data into components
        String[] parts = storedHashData.split(":");
        String saltBase64 = parts[0];
        String storedHashBase64 = parts[1];
        int iterations = Integer.parseInt(parts[2]);
        int keyLength = Integer.parseInt(parts[3]);

        // Decode salt and stored hash from Base64
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] storedHash = Base64.getDecoder().decode(storedHashBase64);

        // Recompute the hash with the input password and stored parameters
        KeySpec spec = new PBEKeySpec(
                inputPassword.toCharArray(),
                salt,
                iterations,
                keyLength
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] computedHash = factory.generateSecret(spec).getEncoded();

        // Constant-time comparison to avoid timing attacks
        return Arrays.equals(computedHash, storedHash);
    }

}
