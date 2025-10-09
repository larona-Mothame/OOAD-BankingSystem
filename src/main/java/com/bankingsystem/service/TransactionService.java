package com.bankingsystem.service;

import com.bankingsystem.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for handling deposits and withdrawals.
 * Delegates to the Bank for underlying account and transaction logic.
 */
public class TransactionService {

    private final BankService bankService = BankService.getInstance();

    /**
     * Deposit into an account.
     */
    public Transaction deposit(String accountNumber, BigDecimal amount, String tellerId) {
        return bankService.getBank().deposit(accountNumber, amount, tellerId);
    }

    /**
     * Withdraw from an account.
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount, String tellerId) {
        return bankService.getBank().withdraw(accountNumber, amount, tellerId);
    }

    /**
     * Retrieve all transactions for an account.
     */
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return bankService.getBank().getTransactionsForAccount(accountNumber);
    }

    // TODO: controllers (e.g., TransactionController) will call these methods from FXML button actions
}
