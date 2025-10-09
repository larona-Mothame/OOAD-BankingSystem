package com.bankingsystem.model;

import java.math.BigDecimal;

/**
 * Interface to be implemented by account types that earn interest.
 */
public interface ApplyInterest {
    /**
     * Apply interest to the account balance.
     * Implementation should update the account's balance appropriately.
     *
     * @return the interest amount that was applied (positive BigDecimal)
     */
    BigDecimal applyInterest();
}
