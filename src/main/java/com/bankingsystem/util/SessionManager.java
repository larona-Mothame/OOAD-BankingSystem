package com.bankingsystem.util;

/**
 * SessionManager is responsible for maintaining the state of the currently logged-in user
 * within the Sediba Financial Banking System.
 *
 * It stores a reference to the active user (either a Teller or a Customer) and their role,
 * allowing any controller in the application to access this session information.
 *
 * This class ensures that only one session is active at a time and
 * enforces that the user role is either "TELLER" or "CUSTOMER".
 *
 * Usage Example:
 * ---------------
 * SessionManager.setCurrentUser(teller, "TELLER");
 * Teller activeTeller = (Teller) SessionManager.getCurrentUser();
 *
 * // On logout
 * SessionManager.clearSession();
 */
public final class SessionManager {

    // Static session data (global for app runtime)
    private static Object currentUser;
    private static String currentRole; // "TELLER" or "CUSTOMER"

    // Prevent instantiation
    private SessionManager() {}

    /**
     * Sets the current user and their role in the session.
     *
     * @param user the logged-in user object (Teller or Customer)
     * @param role the role of the user ("TELLER" or "CUSTOMER")
     */
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

    /**
     * Returns the currently logged-in user (Teller or Customer object).
     *
     * @return the current user object, or null if not logged in.
     */
    public static Object getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns the current role of the logged-in user.
     *
     * @return "TELLER", "CUSTOMER", or null if no session exists.
     */
    public static String getCurrentRole() {
        return currentRole;
    }

    /**
     * Checks if a session is currently active.
     *
     * @return true if a user is logged in; false otherwise.
     */
    public static boolean isLoggedIn() {
        return currentUser != null && currentRole != null;
    }

    /**
     * Clears the current session, used when logging out.
     */
    public static void clearSession() {
        currentUser = null;
        currentRole = null;
    }

    /**
     * Utility helper to check if the active session belongs to a Teller.
     *
     * @return true if current role is TELLER
     */
    public static boolean isTeller() {
        return "TELLER".equalsIgnoreCase(currentRole);
    }

    /**
     * Utility helper to check if the active session belongs to a Customer.
     *
     * @return true if current role is CUSTOMER
     */
    public static boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(currentRole);
    }
}
