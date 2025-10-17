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
    private String password; // Optional; can omit when fetching from DB for security.

    // --- Constructors ---
    public Teller() {}

    public Teller(String tellerId, String firstName, String lastName, String username) {
        this.tellerId = tellerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Teller{" +
                "tellerId='" + tellerId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
