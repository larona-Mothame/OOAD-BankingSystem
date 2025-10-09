package com.bankingsystem.model;

import java.util.Objects;

/**
 * Company / Organization customer.
 */
public class CompanyCustomer extends Customer {

    private final String companyRegistrationNumber;
    private final String primaryContactPerson;
    private final String companyName;

    public CompanyCustomer(String companyName, String companyRegistrationNumber,
                           String primaryContactPerson, String contactNumber,
                           String email, String address) {
        super(companyName, contactNumber, email, address);
        this.companyName = Objects.requireNonNull(companyName, "companyName");
        this.companyRegistrationNumber = Objects.requireNonNull(companyRegistrationNumber, "companyRegistrationNumber");
        this.primaryContactPerson = primaryContactPerson;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public String getPrimaryContactPerson() {
        return primaryContactPerson;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getDisplayName() {
        return companyName + " (Reg: " + companyRegistrationNumber + ")";
    }

    @Override
    public String toString() {
        return "CompanyCustomer{" +
                "customerId='" + customerId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", registrationNumber='" + companyRegistrationNumber + '\'' +
                '}';
    }
}
