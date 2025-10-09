package com.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single deposit or withdrawal transaction against an account.
 * Note: this system only supports single-account transactions (no transfers).
 */
public class Transaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }

    private final String transactionId;
    private final String accountNumber;
    private final Type type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String tellerId; // optional - who performed the transaction

    public Transaction(String accountNumber, Type type, BigDecimal amount, String tellerId) {
        this.transactionId = UUID.randomUUID().toString();
        this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber");
        this.type = Objects.requireNonNull(type, "type");
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.timestamp = LocalDateTime.now();
        this.tellerId = tellerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTellerId() {
        return tellerId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", tellerId='" + tellerId + '\'' +
                '}';
    }
}
