package com.bankingsystem.util;

/**
 * Simple password utility for testing
 */
public class PasswordUtil {

    public static String hashPassword(String plainTextPassword) {
        return plainTextPassword;
    }

    public static boolean verifyPassword(String plainTextPassword, String storedPassword) {
        return plainTextPassword != null && plainTextPassword.equals(storedPassword);
    }
}