package com.bankingsystem.model;

import java.math.BigDecimal;

/**
 * Cheque account: for salary and day-to-day deposits/withdrawals. No overdrafts allowed.
 * Can be for companies or individuals who provide employment details.
 */
public class ChequeAccount extends Account {

    public ChequeAccount(String ownerCustomerId, String branchCode, BigDecimal initialDeposit) {
        super(ownerCustomerId, branchCode, initialDeposit);
        // No minimum in assignment, but could be added here if needed.
    }

    @Override
    public synchronized void withdraw(BigDecimal amount) {
        checkActive();
        // No overdrafts - ensure enough funds
        debit(amount);
    }
}
