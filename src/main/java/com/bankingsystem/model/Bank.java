package com.bankingsystem.model;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bank acts as the central manager of customers, accounts and transactions.
 *
 * NOTE: Persistence (DB) is out of scope here — this is an in-memory representation.
 * Controllers (or DAO layer) will later call Bank methods to create/find/update entities.
 */
public class Bank {

    private final String bankName;
    // Maps for quick lookup
    private final Map<String, Customer> customersById = new ConcurrentHashMap<>();
    private final Map<String, Account> accountsByNumber = new ConcurrentHashMap<>();
    private final Map<String, List<String>> accountsByCustomerId = new ConcurrentHashMap<>();
    private final List<Transaction> transactionLog = Collections.synchronizedList(new ArrayList<>());

    public Bank(String bankName) {
        this.bankName = bankName;
    }


    public Map<String, BigDecimal> applyMonthlyInterestToAll() {
        Map<String, BigDecimal> applied = new HashMap<>();
        for (Account account : accountsByNumber.values()) {
            if (account instanceof ApplyInterest) {
                ApplyInterest ia = (ApplyInterest) account;
                BigDecimal interest = ia.applyInterest();
                applied.put(account.getAccountNumber(), interest);
            }
        }
        return applied;
    }






}
