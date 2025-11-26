package com.bankingsystem.service;

import com.bankingsystem.model.CompanyCustomer;
import com.bankingsystem.util.PasswordUtil;
import com.bankingsystem.database.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AccountService {


    public String createIndividualAccount(String firstName, String lastName, String nationalId,
                                          LocalDate dateOfBirth, String gender, String email,
                                          String phoneNumber, String address, String accountType,
                                          BigDecimal initialDeposit, String tellerId) {
        return createNewAccount(firstName, lastName, nationalId, dateOfBirth, gender,
                email, phoneNumber, address, accountType, initialDeposit, tellerId);
    }

    public String createCompanyAccount(CompanyCustomer company, String accountType,
                                       BigDecimal initialDeposit, String tellerId) {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check if the company already exists
            String companyId = findCompanyByRegistrationNumber(company.getRegistrationNumber());

            if (companyId == null) {
                // Step 2: Generate a new customer ID
                companyId = generateCustomerId();

                // Step 3: Insert a minimal customer record for the company
                createCustomerForCompany(conn, companyId, company);

                // Step 4: Insert the company details
                createCompany(conn, companyId, company);
            }

            // Step 5: Create the account for the company
            String accountNumber = generateAccountNumber(accountType);
            createAccount(conn, accountNumber, companyId, accountType, initialDeposit);

            conn.commit();
            return accountNumber;

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void createCustomerForCompany(Connection conn, String customerId, CompanyCustomer company) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, username, password_hash, is_active, created_date) " +
                "VALUES (?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Use company name as first_name and a placeholder for last_name
            String firstName = company.getCompanyName();
            String lastName = "Company";

            stmt.setString(1, customerId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, "company_" + customerId); // unique username
            stmt.setString(5, PasswordUtil.hashPassword("Welcome123")); // default password
            stmt.executeUpdate();
        }
    }



    private String createNewAccount(String firstName, String lastName, String nationalId,
                                    LocalDate dateOfBirth, String gender, String email,
                                    String phoneNumber, String address, String accountType,
                                    BigDecimal initialDeposit, String tellerId) {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String customerId = findCustomerByNationalId(nationalId);
            if (customerId == null) {
                customerId = generateCustomerId();
                createCustomer(conn, customerId, firstName, lastName, nationalId,
                        dateOfBirth, gender, email, phoneNumber, address);
            }

            String accountNumber = generateAccountNumber(accountType);
            createAccount(conn, accountNumber, customerId, accountType, initialDeposit);

            conn.commit();
            return accountNumber;

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean isUniqueCustomer(String idNumber, String email, String phone) {
        String sql = "SELECT COUNT(*) FROM customers WHERE national_id = ? OR email = ? OR phone_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNumber);
            ps.setString(2, email);
            ps.setString(3, phone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }

        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private String findCustomerByNationalId(String nationalId) {
        String sql = "SELECT customer_id FROM customers WHERE national_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nationalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("customer_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private String findCompanyByRegistrationNumber(String regNumber) {
        String sql = "SELECT customer_id FROM company_customers WHERE registration_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, regNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void createCustomer(Connection conn, String customerId, String firstName, String lastName,
                                String nationalId, LocalDate dateOfBirth, String gender, String email,
                                String phoneNumber, String address) throws SQLException {

        String sql = "INSERT INTO customers (customer_id, first_name, last_name, username, password_hash, " +
                "national_id, date_of_birth, gender, email, phone_number, address, is_active, created_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String username = generateUsername(firstName, lastName);
            String passwordHash = PasswordUtil.hashPassword("Welcome123");

            stmt.setString(1, customerId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, username);
            stmt.setString(5, passwordHash);
            stmt.setString(6, nationalId);
            stmt.setDate(7, java.sql.Date.valueOf(dateOfBirth));
            stmt.setString(8, gender != null ? gender.substring(0, 1) : null);
            stmt.setString(9, email);
            stmt.setString(10, phoneNumber);
            stmt.setString(11, address);

            stmt.executeUpdate();
        }
    }

    private void createCompany(Connection conn, String companyId, CompanyCustomer company) throws SQLException {
        String sql = "INSERT INTO company_customers (customer_id, registration_number, primary_contact) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, companyId);
            stmt.setString(2, company.getRegistrationNumber());
            stmt.setString(3, company.getPrimaryContact());
            stmt.executeUpdate();
        }
    }


    private String generateCustomerId() { return "CUST" + (System.currentTimeMillis() % 1000000); }

    private void createAccount(Connection conn, String accountNumber, String customerId,
                               String accountType, BigDecimal initialDeposit) throws SQLException {

        String dbAccountType = mapAccountTypeToDB(accountType);

        String sql = "INSERT INTO accounts (account_id, account_number, owner_customer_id, account_type, balance, branch_code, date_opened, status) " +
                "VALUES (?, ?, ?, ?, ?, 'B001', CURRENT_TIMESTAMP, 'ACTIVE')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, accountNumber);
            stmt.setString(3, customerId);
            stmt.setString(4, dbAccountType);
            stmt.setBigDecimal(5, initialDeposit);
            stmt.executeUpdate();
        }
    }

    private String generateUsername(String firstName, String lastName) {
        String base = (firstName + "." + lastName).toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-z.]", "");
        String suffix = String.format("%03d", (int)(Math.random() * 1000));
        if (base.length() > 46) base = base.substring(0, 46);
        return base + suffix;
    }

    private String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType != null ? accountType.toUpperCase() : "SAVINGS") {
            case "SAVINGS": prefix = "SAV"; break;
            case "INVESTMENT": prefix = "INV"; break;
            case "CHEQUE":
            case "CHEQUING": prefix = "CHQ"; break;
            default: prefix = "SAV";
        }
        long randomNum = (long)(Math.random() * 900000L) + 100000L;
        return prefix + randomNum;
    }

    private String mapAccountTypeToDB(String accountType) {
        if (accountType == null) return "SAVINGS";
        switch (accountType.toUpperCase()) {
            case "SAVINGS": return "SAVINGS";
            case "INVESTMENT": return "INVESTMENT";
            case "CHEQUE":
            case "CHEQUING": return "CHEQUE";
            default: return "SAVINGS";
        }
    }


}
