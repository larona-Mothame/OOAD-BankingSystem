package com.bankingsystem.db;

import com.bankingsystem.database.DBConnection;
import com.bankingsystem.model.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TransactionDAO {

    public List<Transaction> findRecentTransactionsByCustomerId(String customerId, int limit) {
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "WHERE a.owner_customer_id = ? " +
                "ORDER BY t.transaction_timestamp DESC " +
                "FETCH FIRST ? ROWS ONLY";

        return findTransactions(sql, customerId, limit);
    }

    public List<Transaction> findTransactionsByCustomerId(String customerId) {
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "WHERE a.owner_customer_id = ? " +
                "ORDER BY t.transaction_timestamp DESC";

        return findTransactions(sql, customerId, 0);
    }

    public List<Transaction> findTransactionsByCustomerIdAndPeriod(String customerId, int days) {
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "WHERE a.owner_customer_id = ? AND t.transaction_timestamp >= CURRENT_TIMESTAMP - ? " +
                "ORDER BY t.transaction_timestamp DESC";

        return findTransactions(sql, customerId, days);
    }

    public List<Transaction> findTransactionsByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = createTransactionFromResultSet(rs);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private List<Transaction> findTransactions(String sql, String customerId, int param) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            if (param > 0) {
                stmt.setInt(2, param);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = createTransactionFromResultSet(rs);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        String transactionId = rs.getString("transaction_id");
        String accountNumber = rs.getString("account_number");
        String transactionType = rs.getString("transaction_type");
        BigDecimal amount = rs.getBigDecimal("amount");
        Timestamp timestamp = rs.getTimestamp("transaction_timestamp");
        String tellerId = rs.getString("teller_id");

        // Convert to LocalDateTime
        LocalDateTime localDateTime = timestamp.toLocalDateTime();

        // Use the new constructor that accepts all database fields
        return new Transaction(transactionId, accountNumber,
                Transaction.Type.valueOf(transactionType),
                amount, localDateTime, tellerId);
    }

    private String generateTransactionId() {
        String getMaxIdSql = "SELECT MAX(transaction_id) FROM transactions WHERE transaction_id LIKE 'TXN%'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getMaxIdSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String maxId = rs.getString(1);
                System.out.println("DEBUG: Current max transaction ID: " + maxId);

                if (maxId != null && maxId.startsWith("TXN")) {
                    try {
                        // Extract the numeric part and increment
                        String numericPart = maxId.substring(3);
                        int currentNum = Integer.parseInt(numericPart);
                        String nextId = "TXN" + String.format("%03d", currentNum + 1);
                        System.out.println("DEBUG: Generated next transaction ID: " + nextId);
                        return nextId;
                    } catch (NumberFormatException e) {
                        System.err.println("ERROR: Could not parse numeric part from: " + maxId);
                        // Fall through to default generation
                    }
                }
            }

            // No existing TXN IDs or parsing failed, start from 1
            String firstId = "TXN001";
            System.out.println("DEBUG: No existing TXN IDs, starting with: " + firstId);
            return firstId;

        } catch (SQLException e) {
            System.err.println("ERROR generating transaction ID: " + e.getMessage());
            e.printStackTrace();
            // Fallback: timestamp-based ID
            String fallbackId = "TXN" + System.currentTimeMillis();
            System.out.println("DEBUG: Using fallback ID: " + fallbackId);
            return fallbackId;
        }
    }

    public boolean saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, transaction_timestamp, teller_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Generate sequential ID
            String transactionId = generateTransactionId();

            // Set the generated ID on the transaction object
            transaction.setTransactionId(transactionId);

            stmt.setString(1, transactionId);
            stmt.setString(2, transaction.getAccountNumber());
            stmt.setString(3, transaction.getType().toString());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            stmt.setString(6, transaction.getTellerId());

            int rowsInserted = stmt.executeUpdate();
            System.out.println("DEBUG: Saved transaction with ID: " + transactionId +
                    ", Account: " + transaction.getAccountNumber() +
                    ", Type: " + transaction.getType() +
                    ", Amount: " + transaction.getAmount());
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("ERROR saving transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}