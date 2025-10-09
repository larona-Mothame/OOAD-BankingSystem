package com.bankingsystem.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Investment account: deposits and withdrawals allowed but must maintain minimum balance.
 * Minimum opening and maintenance balance: BWP 500. Applies 5% monthly interest (0.05).
 */
public class InvestmentAccount extends Account implements ApplyInterest {

    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("500.00");
    // 5% monthly (as defined in assignment)
    private static final BigDecimal MONTHLY_RATE = new BigDecimal("0.05");

    public InvestmentAccount(String ownerCustomerId, String branchCode, BigDecimal initialDeposit) {
        super(ownerCustomerId, branchCode, initialDeposit);
        if (initialDeposit == null || initialDeposit.compareTo(MINIMUM_BALANCE) < 0) {
            throw new IllegalArgumentException("Investment account requires an initial deposit of at least " + MINIMUM_BALANCE);
        }
    }

    @Override
    public synchronized void withdraw(BigDecimal amount) {
        checkActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        // Ensure after withdrawal, balance >= MINIMUM_BALANCE
        if (balance.subtract(amount).compareTo(MINIMUM_BALANCE) < 0) {
            throw new IllegalStateException("Withdrawal would breach minimum balance of " + MINIMUM_BALANCE);
        }
        debit(amount);
    }

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

    public static BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
}
