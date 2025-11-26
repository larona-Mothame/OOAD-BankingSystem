package com.bankingsystem.model;

import java.util.Objects;

/**
 * Company / Organization customer.
 */
public class CompanyCustomer extends Customer {

    private String companyRegistrationNumber;
    private String primaryContactPerson;

    public CompanyCustomer(String companyName, String companyRegistrationNumber,
                           String primaryContactPerson, String contactNumber,
                           String email, String address) {
        super(companyName, contactNumber, email, address);
        this.companyRegistrationNumber = Objects.requireNonNull(companyRegistrationNumber, "companyRegistrationNumber");
        this.primaryContactPerson = primaryContactPerson;
    }

    // Constructor for database loading
    public CompanyCustomer(String customerId, String companyName, String contactNumber,
                           String email, String address, String username, String passwordHash,
                           boolean isActive, String companyRegistrationNumber, String primaryContactPerson) {
        super(customerId, companyName, contactNumber, email, address, username, passwordHash, isActive);
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.primaryContactPerson = primaryContactPerson;
    }


    public String getPrimaryContact() {
        return primaryContactPerson;
    }

    public String getCompanyName() {
        return name; // Company name is stored in the name field
    }

    @Override
    public String getDisplayName() {
        return name + " (Reg: " + companyRegistrationNumber + ")";
    }

    @Override
    public String toString() {
        return "CompanyCustomer{" +
                "customerId='" + customerId + '\'' +
                ", companyName='" + name + '\'' +
                ", registrationNumber='" + companyRegistrationNumber + '\'' +
                ", primaryContactPerson='" + primaryContactPerson + '\'' +
                '}';
    }

    public String getRegistrationNumber() {
        return companyRegistrationNumber;
    }



}