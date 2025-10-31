
package com.bankingsystem.model;

import java.math.BigDecimal;

public class AccountWithCustomer {
    private final String accountNumber;
    private final String customerName;
    private final String accountType;
    private final BigDecimal balance;
    private final String status;
    private final String nationalId;
    private final String phoneNumber;
    private final String email;

    public AccountWithCustomer(String accountNumber, String customerName, String accountType,
                               BigDecimal balance, String status, String nationalId,
                               String phoneNumber, String email) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerName() { return customerName; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getStatus() { return status; }
    public String getNationalId() { return nationalId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
}