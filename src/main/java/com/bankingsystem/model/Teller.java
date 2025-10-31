package com.bankingsystem.model;

/**
 * Represents a Teller (bank employee) in the Sediba Financial Banking System.
 * A Teller can open accounts, manage customers, and perform basic operations.
 */
public class Teller {

    private String tellerId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String passwordHash; // For database storage
    private boolean isActive;    // To track if teller account is active


    public Teller(String tellerId, String firstName, String lastName, String username) {
        this.tellerId = tellerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isActive = true; // Default to active
    }

    // New constructor for database operations
    public Teller(String tellerId, String firstName, String lastName, String username,
                  String passwordHash, boolean isActive) {
        this.tellerId = tellerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
    }

    // --- Getters & Setters ---
    public String getTellerId() {
        return tellerId;
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Teller{" +
                "tellerId='" + tellerId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    // Utility method to check if teller can login
    public boolean canLogin() {
        return isActive && passwordHash != null && !passwordHash.trim().isEmpty();
    }

    // Optional: Add a method to get display name (username or full name)
    public String getDisplayName() {
        return username != null ? username : getFullName();
    }

}