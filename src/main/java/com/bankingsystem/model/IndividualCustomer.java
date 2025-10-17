package com.bankingsystem.model;

import java.time.LocalDate;

/**
 * Represents an individual customer in the system.
 */
public class IndividualCustomer extends Customer {

    private String nationalId;
    private LocalDate dateOfBirth;

    // Full constructor
    public IndividualCustomer(String name, String contactNumber, String email, String address,
                              String nationalId, LocalDate dateOfBirth) {
        super(name, contactNumber, email, address);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
    }

    // ✅ Lightweight constructor for login/session use
    public IndividualCustomer(String name, String contactNumber, String email, String address) {
        super(name, contactNumber, email, address);
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
