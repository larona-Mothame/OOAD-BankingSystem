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

    public String getBankName() {
        return bankName;
    }

    // ---------------- Customer operations ----------------

    public Customer addCustomer(Customer customer) {
        Objects.requireNonNull(customer, "customer");
        customersById.put(customer.getCustomerId(), customer);
        accountsByCustomerId.putIfAbsent(customer.getCustomerId(), Collections.synchronizedList(new ArrayList<>()));
        return customer;
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return Optional.ofNullable(customersById.get(customerId));
    }

    public List<Customer> searchCustomersByName(String nameFragment) {
        if (nameFragment == null || nameFragment.trim().isEmpty()) {
            return new ArrayList<>(customersById.values());
        }
        String lower = nameFragment.toLowerCase();
        return customersById.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ---------------- Account operations ----------------

    public Account addAccount(Account account) {
        Objects.requireNonNull(account, "account");
        accountsByNumber.put(account.getAccountNumber(), account);
        accountsByCustomerId.putIfAbsent(account.getOwnerCustomerId(), Collections.synchronizedList(new ArrayList<>()));
        accountsByCustomerId.get(account.getOwnerCustomerId()).add(account.getAccountNumber());
        return account;
    }

    public Optional<Account> findAccountByNumber(String accountNumber) {
        return Optional.ofNullable(accountsByNumber.get(accountNumber));
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        List<String> acctNums = accountsByCustomerId.get(customerId);
        if (acctNums == null) return Collections.emptyList();
        return acctNums.stream()
                .map(accountsByNumber::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ---------------- Transaction operations ----------------

    /**
     * Create and record a deposit transaction and apply it to the account.
     *
     * @param accountNumber target account
     * @param amount        amount to deposit
     * @param tellerId      teller performing the deposit (optional)
     * @return created Transaction
     */
    public Transaction deposit(String accountNumber, BigDecimal amount, String tellerId) {
        Account account = accountsByNumber.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        account.deposit(amount);
        Transaction tx = new Transaction(accountNumber, Transaction.Type.DEPOSIT, amount, tellerId);
        transactionLog.add(tx);
        return tx;
    }

    /**
     * Create and record a withdrawal transaction and apply it to the account.
     *
     * @param accountNumber target account
     * @param amount        amount to withdraw
     * @param tellerId      teller performing the withdrawal (optional)
     * @return created Transaction
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount, String tellerId) {
        Account account = accountsByNumber.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        account.withdraw(amount);
        Transaction tx = new Transaction(accountNumber, Transaction.Type.WITHDRAWAL, amount, tellerId);
        transactionLog.add(tx);
        return tx;
    }

    /**
     * Return all transactions for a given account.
     */
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        synchronized (transactionLog) {
            return transactionLog.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Apply interest to all accounts that implement ApplyInterest.
     * Returns a map of accountNumber -> interestApplied.
     */
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

    /**
     * Simple daily report example: list of all transactions since provided timestamp.
     */
    public List<Transaction> getTransactionsSince(Date since) {
        Objects.requireNonNull(since);
        synchronized (transactionLog) {
            return transactionLog.stream()
                    .filter(t -> java.sql.Timestamp.valueOf(t.getTimestamp()).after(since))
                    .collect(Collectors.toList());
        }
    }

    // ---------------- Utility / housekeeping ----------------

    /**
     * Close an account (only allowed if balance zero). Removes mapping but keeps transaction history.
     * Controllers should ensure any UI confirmations before calling.
     */
    public void closeAccount(String accountNumber) {
        Account account = accountsByNumber.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        account.closeAccount();
        // we keep the account in the map for reporting/history; controllers can filter by status
    }

    // TODO: Add persistence integration with DB layer (DAO) - controllers / service layer will call DAO to persist.

}
