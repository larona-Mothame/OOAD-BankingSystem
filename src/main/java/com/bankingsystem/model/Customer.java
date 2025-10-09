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

    public Customer(String name, String contactNumber, String email, String address) {
        this.customerId = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "name");
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
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
                '}';
    }
}
