package com.bankingsystem.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Individual customer.
 */
public class IndividualCustomer extends Customer {

    private final String nationalId;
    private final LocalDate dateOfBirth;

    public IndividualCustomer(String name, String contactNumber, String email, String address,
                              String nationalId, LocalDate dateOfBirth) {
        super(name, contactNumber, email, address);
        this.nationalId = Objects.requireNonNull(nationalId, "nationalId");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "dateOfBirth");
    }

    public String getNationalId() {
        return nationalId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public String getDisplayName() {
        return name + " (ID: " + nationalId + ")";
    }

    @Override
    public String toString() {
        return "IndividualCustomer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
