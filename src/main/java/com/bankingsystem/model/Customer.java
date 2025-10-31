package com.bankingsystem.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for customers.
 */
public abstract class Customer {

    protected final String customerId;
    protected String name;
    protected String contactNumber;
    protected String email;
    protected String address;

    // Added for authentication and session management
    protected String username;
    protected String passwordHash; // Changed from password to passwordHash
    protected boolean isActive;    // Added for account status

    public Customer(String name, String contactNumber, String email, String address) {
        this.customerId = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "name");
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.isActive = true; // Default to active
    }

    // Constructor for database loading
    public Customer(String customerId, String name, String contactNumber, String email,
                    String address, String username, String passwordHash, boolean isActive) {
        this.customerId = customerId;
        this.name = Objects.requireNonNull(name, "name");
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // --- Authentication methods ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    /**
     * Full name or company name to be shown in session context.
     */
    public String getFullName() {
        return name != null ? name : "Unnamed Customer";
    }

    /**
     * Human-readable display for the customer.
     */
    public abstract String getDisplayName();

    /**
     * Utility method to check if customer can login
     */
    public boolean canLogin() {
        return isActive && passwordHash != null && !passwordHash.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}