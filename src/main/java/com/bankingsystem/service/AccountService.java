package com.bankingsystem.service;

import com.bankingsystem.db.AccountDAO;
import com.bankingsystem.db.CustomerDAO;
import com.bankingsystem.model.*;
import com.bankingsystem.util.PasswordUtil;
import com.bankingsystem.database.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class AccountService {
    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    public String createNewAccount(String firstName, String lastName, String nationalId,
                                   LocalDate dateOfBirth, String gender, String email,
                                   String phoneNumber, String address, String accountType,
                                   BigDecimal initialDeposit, String tellerId) {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check if customer already exists
            String customerId = findCustomerByNationalId(nationalId);

            if (customerId == null) {
                // 2. Create new customer
                customerId = generateCustomerId();
                createCustomer(conn, customerId, firstName, lastName, nationalId, dateOfBirth,
                        gender, email, phoneNumber, address);
            } else {
                System.out.println("DEBUG: Customer already exists with ID: " + customerId);
            }

            // 3. Create account
            String accountNumber = generateAccountNumber(accountType);
            createAccount(conn, accountNumber, customerId, accountType, initialDeposit);

            conn.commit();
            System.out.println("DEBUG: Successfully created account: " + accountNumber);
            return accountNumber;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("DEBUG: Transaction rolled back due to error");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String findCustomerByNationalId(String nationalId) {
        String sql = "SELECT customer_id FROM customers WHERE national_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nationalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateCustomerId() {
        return "CUST" + (System.currentTimeMillis() % 1000000);
    }

    private void createCustomer(Connection conn, String customerId, String firstName, String lastName,
                                String nationalId, LocalDate dateOfBirth, String gender, String email,
                                String phoneNumber, String address) throws SQLException {

        // Generate username from first name + last name
        String username = generateUsername(firstName, lastName);
        // Use the simple PasswordUtil that doesn't hash
        String tempPassword = "Welcome123";
        String passwordHash = PasswordUtil.hashPassword(tempPassword);

        String sql = "INSERT INTO customers (customer_id, first_name, last_name, username, password_hash, " +
                "national_id, date_of_birth, gender, email, phone_number, address, is_active, created_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, username);
            stmt.setString(5, passwordHash);
            stmt.setString(6, nationalId);
            stmt.setDate(7, java.sql.Date.valueOf(dateOfBirth));
            stmt.setString(8, gender != null ? gender.substring(0, 1) : null); // Store only first char (M/F/O)
            stmt.setString(9, email);
            stmt.setString(10, phoneNumber);
            stmt.setString(11, address);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Created customer - ID: " + customerId + ", Username: " + username + ", Rows: " + rowsAffected);
        }
    }

    private String generateUsername(String firstName, String lastName) {
        // Create username from first name + last name + random 3 digits
        String baseUsername = (firstName + "." + lastName).toLowerCase()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-z.]", "");

        String randomSuffix = String.format("%03d", (int)(Math.random() * 1000));

        // Ensure username is not longer than 50 characters
        String username = baseUsername;
        if (username.length() > 46) {
            username = username.substring(0, 46);
        }
        username += randomSuffix;

        return username;
    }

    private void createAccount(Connection conn, String accountNumber, String customerId,
                               String accountType, BigDecimal initialDeposit) throws SQLException {

        // Map the account type to database values
        String dbAccountType = mapAccountTypeToDB(accountType);

        // Use the correct column names from your ACCOUNTS table
        String sql = "INSERT INTO accounts (account_id, account_number, owner_customer_id, account_type, " +
                "balance, branch_code, date_opened, status) " +
                "VALUES (?, ?, ?, ?, ?, 'B001', CURRENT_TIMESTAMP, 'ACTIVE')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Use accountNumber as account_id (or generate a separate one)
            stmt.setString(1, accountNumber); // Using accountNumber as account_id
            stmt.setString(2, accountNumber);
            stmt.setString(3, customerId);
            stmt.setString(4, dbAccountType);
            stmt.setBigDecimal(5, initialDeposit);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Created account - ID: " + accountNumber + ", Number: " + accountNumber +
                    ", Type: " + dbAccountType + ", Rows: " + rowsAffected);
        }
    }

    private String mapAccountTypeToDB(String accountType) {
        if (accountType == null) return "SAVINGS";

        switch (accountType.toUpperCase()) {
            case "SAVINGS": return "SAVINGS";
            case "INVESTMENT": return "INVESTMENT";
            case "CHEQUE":
            case "CHEQUING":
                return "CHEQUE";
            default: return "SAVINGS";
        }
    }
    public boolean isUniqueCustomer(String idNumber, String email, String phone) {
        String sql = "SELECT COUNT(*) FROM customers WHERE national_id  = ? OR email = ? OR phone_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNumber);
            ps.setString(2, email);
            ps.setString(3, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0; // true if no existing customer found
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // if error occurs, assume not unique
    }


    private String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType != null ? accountType.toUpperCase() : "SAVINGS") {
            case "SAVINGS": prefix = "SAV"; break;
            case "INVESTMENT": prefix = "INV"; break;
            case "CHEQUE":
            case "CHEQUING":
                prefix = "CHQ"; break;
            default: prefix = "SAV";
        }
        long randomNum = (long)(Math.random() * 900000L) + 100000L;
        return prefix + randomNum;
    }
}