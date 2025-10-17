package com.bankingsystem.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for customers.
 *
 * Extended to support authentication and session management.
 * Subclasses (IndividualCustomer, CompanyCustomer) may add more specific fields.
 */
public abstract class Customer {

    protected final String customerId;
    protected String name;
    protected String contactNumber;
    protected String email;
    protected String address;

    // Added for authentication and session management
    protected String username;
    protected String password;

    public Customer(String name, String contactNumber, String email, String address) {
        this.customerId = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "name");
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    // Optional constructor if you ever initialize with username/password from DB
    public Customer(String name, String contactNumber, String email, String address,
                    String username, String password) {
        this(name, contactNumber, email, address);
        this.username = username;
        this.password = password;
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

    // --- Added for authentication ---

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

    /**
     * Full name or company name to be shown in session context.
     */
    public String getFullName() {
        return name != null ? name : "Unnamed Customer";
    }

    /**
     * Human-readable display for the customer.
     */
    /**
     * Human-readable display for the customer.
     */
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
