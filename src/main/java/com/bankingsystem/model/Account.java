package com.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract Account class with shared behavior for all account types.
 */
public abstract class Account {

    public enum Status {
        ACTIVE,
        DORMANT,
        CLOSED
    }

    protected final String accountNumber;
    protected final String ownerCustomerId; // owner reference (customerId)
    protected BigDecimal balance;
    protected final String branchCode;
    protected final LocalDateTime dateOpened;
    protected Status status;

    protected Account(String ownerCustomerId, String branchCode, BigDecimal initialDeposit) {
        this.accountNumber = generateAccountNumber(branchCode);
        this.ownerCustomerId = Objects.requireNonNull(ownerCustomerId, "ownerCustomerId");
        this.branchCode = branchCode;
        this.balance = initialDeposit == null ? BigDecimal.ZERO : initialDeposit;
        this.dateOpened = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    protected static String generateAccountNumber(String branchCode) {
        String uuidShort = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (branchCode == null || branchCode.trim().isEmpty()) {
            return "AC-" + uuidShort;
        }
        return branchCode.toUpperCase() + "-" + uuidShort;
    }

    public String getAccountNumber() {
        return accountNumber;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public synchronized void deposit(BigDecimal amount) {
        checkActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    protected synchronized void debit(BigDecimal amount) {
        checkActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }

    public abstract void withdraw(BigDecimal amount);


    protected void checkActive() {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + status);
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerCustomerId='" + ownerCustomerId + '\'' +
                ", balance=" + balance +
                ", branchCode='" + branchCode + '\'' +
                ", dateOpened=" + dateOpened +
                ", status=" + status +
                '}';
    }
}
