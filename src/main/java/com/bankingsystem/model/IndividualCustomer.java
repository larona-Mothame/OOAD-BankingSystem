package com.bankingsystem.model;

import java.time.LocalDate;
import java.util.Date;

/**
 * Represents an individual customer in the system.
 */
public class IndividualCustomer extends Customer {

    private String nationalId;
    private LocalDate dateOfBirth;

    // Full constructor for application use
    public IndividualCustomer(String name, String contactNumber, String email, String address,
                              String nationalId, LocalDate dateOfBirth) {
        super(name, contactNumber, email, address);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
    }

    // Lightweight constructor for login/session use
    public IndividualCustomer(String name, String contactNumber, String email, String address) {
        super(name, contactNumber, email, address);
    }

    // Constructor for database loading - FIXED VERSION
    public IndividualCustomer(String customerId, String name, String contactNumber, String email,
                              String address, String username, String passwordHash, boolean isActive,
                              String nationalId, Date dateOfBirth) {
        super(customerId, name, contactNumber, email, address, username, passwordHash, isActive);
        this.nationalId = nationalId;

        // FIX: Safe conversion from java.sql.Date to LocalDate
        if (dateOfBirth != null) {
            // Check if it's a java.sql.Date (which doesn't support toInstant())
            if (dateOfBirth instanceof java.sql.Date) {
                this.dateOfBirth = ((java.sql.Date) dateOfBirth).toLocalDate();
            } else {
                // It's a java.util.Date, use the old method
                this.dateOfBirth = new java.sql.Date(dateOfBirth.getTime()).toLocalDate();
            }
        } else {
            this.dateOfBirth = null;
        }
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getDisplayName() {
        return getFullName() + " (Individual)";
    }
}