package com.bankingsystem.service;

import com.bankingsystem.model.Bank;

/**
 * Singleton service providing centralized access to the Bank instance.
 * Controllers and other services should use BankService.getInstance() to access shared Bank data.
 */
public class BankService {

    private static BankService instance;
    private final Bank bank;

    private BankService() {
        this.bank = new Bank("Sediba Financial");
    }

    public static synchronized BankService getInstance() {
        if (instance == null) {
            instance = new BankService();
        }
        return instance;
    }

    public Bank getBank() {
        return bank;
    }
}
