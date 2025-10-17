package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.ChequeAccount;
import com.bankingsystem.model.InvestmentAccount;
import com.bankingsystem.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for creating and managing accounts.
 * Provides high-level account operations for controllers.
 */
public class AccountService {

    private final BankService bankService = BankService.getInstance();
    private static final BigDecimal MIN_INVESTMENT_DEPOSIT = new BigDecimal("500");

    /**
     * Open a new Savings Account.
     */
    public SavingsAccount openSavingsAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        validateAccountParameters(customerId, branchCode, initialDeposit);

        SavingsAccount account = new SavingsAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
    }

    /**
     * Open a new Investment Account (requires min deposit of 500).
     */
    public InvestmentAccount openInvestmentAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        validateAccountParameters(customerId, branchCode, initialDeposit);

        if (initialDeposit.compareTo(MIN_INVESTMENT_DEPOSIT) < 0) {
            throw new IllegalArgumentException("Investment account requires minimum deposit of " + MIN_INVESTMENT_DEPOSIT);
        }

        InvestmentAccount account = new InvestmentAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
    }

    /**
     * Open a new Cheque Account.
     */
    public ChequeAccount openChequeAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        validateAccountParameters(customerId, branchCode, initialDeposit);

        ChequeAccount account = new ChequeAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
    }

    private void validateAccountParameters(String customerId, String branchCode, BigDecimal initialDeposit) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Branch code cannot be null or empty");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be null or negative");
        }
    }

    public Optional<Account> findAccountByNumber(String accountNumber) {
        return bankService.getBank().findAccountByNumber(accountNumber);
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        return bankService.getBank().getAccountsForCustomer(customerId);
    }

    public void closeAccount(String accountNumber) {
        bankService.getBank().closeAccount(accountNumber);
    }

    public Map<String, BigDecimal> applyInterestToAllAccounts() {
        return bankService.getBank().applyMonthlyInterestToAll();
    }
}