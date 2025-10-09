package com.bankingsystem.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Savings account: deposits allowed; withdrawals not allowed except at closure.
 * Interest: 0.05% monthly (i.e., 0.0005 multiplier).
 */
public class SavingsAccount extends Account implements ApplyInterest {

    // monthly interest percentage (0.05% = 0.0005)
    private static final BigDecimal MONTHLY_RATE = new BigDecimal("0.0005");

    public SavingsAccount(String ownerCustomerId, String branchCode, BigDecimal initialDeposit) {
        super(ownerCustomerId, branchCode, initialDeposit);
    }

    /**
     * Withdrawals from savings are not allowed except when closing account.
     * Attempting to withdraw will throw an exception.
     */
    @Override
    public void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException("Withdrawals from savings accounts are not allowed. Close the account to access funds.");
    }

    /**
     * Apply monthly interest (simple interest for demonstration).
     * Interest amount is rounded to 2 decimal places (currency).
     *
     * @return interest amount applied
     */
    @Override
    public synchronized BigDecimal applyInterest() {
        checkActive();
        BigDecimal interest = balance.multiply(MONTHLY_RATE);
        interest = interest.setScale(2, RoundingMode.HALF_UP);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.add(interest);
        }
        return interest;
    }
}
