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

    /**
     * Generate an account number. For readability it uses a prefix branchCode-UUID short.
     * Controllers or DB may override later if a different scheme is required.
     */
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

    public String getOwnerCustomerId() {
        return ownerCustomerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Deposit money into account. Positive amounts required.
     *
     * @param amount amount to deposit (must be > 0)
     * @throws IllegalArgumentException if amount <= 0
     * @throws IllegalStateException if account is not active
     */
    public synchronized void deposit(BigDecimal amount) {
        checkActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    /**
     * Withdraw money from account. Subclasses implement withdrawal rules and call this to perform actual debit.
     *
     * @param amount amount to withdraw
     * @throws IllegalArgumentException if amount <= 0
     * @throws IllegalStateException    if account is not active
     */
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

    /**
     * Default withdraw method - subclasses override to enforce specific rules.
     *
     * @param amount amount to withdraw
     */
    public abstract void withdraw(BigDecimal amount);

    /**
     * Close the account. Default behavior: only allow if balance is zero.
     *
     * Subclasses may override if special conditions apply (e.g., disallow closing if min balance).
     */
    public synchronized void closeAccount() {
        if (status == Status.CLOSED) return;
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account balance must be zero to close account");
        }
        status = Status.CLOSED;
    }

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
