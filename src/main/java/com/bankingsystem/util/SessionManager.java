package com.bankingsystem.util;


public final class SessionManager {

    // Static session data (global for app runtime)
    private static Object currentUser;
    private static String currentRole; // "TELLER" or "CUSTOMER"

    // Prevent instantiation
    private SessionManager() {}


    public static void setCurrentUser(Object user, String role) {
        if (user == null || role == null) {
            throw new IllegalArgumentException("User and role must not be null");
        }

        if (!role.equalsIgnoreCase("TELLER") && !role.equalsIgnoreCase("CUSTOMER")) {
            throw new IllegalArgumentException("Invalid role: must be TELLER or CUSTOMER");
        }

        currentUser = user;
        currentRole = role.toUpperCase();
    }


    public static Object getCurrentUser() {
        return currentUser;
    }


    public static String getCurrentRole() {
        return currentRole;
    }

    public static boolean isLoggedIn() {
        return currentUser != null && currentRole != null;
    }


    public static void clearSession() {
        currentUser = null;
        currentRole = null;
    }

    public static boolean isTeller() {
        return "TELLER".equalsIgnoreCase(currentRole);
    }


    public static boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(currentRole);
    }
}
