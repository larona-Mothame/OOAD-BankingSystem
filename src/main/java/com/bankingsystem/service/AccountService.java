package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.ChequeAccount;
import com.bankingsystem.model.InvestmentAccount;
import com.bankingsystem.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class package com.bankingsystem.service;

import com.bankingsystem.model.*;
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

    /**
     * Open a new Savings Account.
     */
    public SavingsAccount openSavingsAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        SavingsAccount account = new SavingsAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
    }

    /**
     * Open a new Investment Account (requires min deposit of 500).
     */
    public InvestmentAccount openInvestmentAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        InvestmentAccount account = new InvestmentAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
    }

    /**
     * Open a new Cheque Account.
     */
    public ChequeAccount openChequeAccount(String customerId, String branchCode, BigDecimal initialDeposit) {
        ChequeAccount account = new ChequeAccount(customerId, branchCode, initialDeposit);
        bankService.getBank().addAccount(account);
        return account;
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

    // TODO: later hook for GUI actions (e.g., interest button triggers this service)
}
AccountService {
}
