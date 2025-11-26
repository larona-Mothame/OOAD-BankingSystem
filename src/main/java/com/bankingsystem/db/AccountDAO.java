package com.bankingsystem.db;
import com.bankingsystem.database.DBConnection;
import com.bankingsystem.model.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> findAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE owner_customer_id = ? AND status = 'ACTIVE' ORDER BY date_opened DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Account account = createAccountFromResultSet(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private Account createAccountFromResultSet(ResultSet rs) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String ownerCustomerId = rs.getString("owner_customer_id");
        String branchCode = rs.getString("branch_code");
        BigDecimal balance = rs.getBigDecimal("balance");
        BigDecimal initialDeposit = balance;

        Account account = null;

        switch (accountType.toUpperCase()) {
            case "CHEQUE":
                account = new ChequeAccount(ownerCustomerId, branchCode, initialDeposit);
                break;
            case "SAVINGS":
                account = new SavingsAccount(ownerCustomerId, branchCode, initialDeposit);
                break;
            case "INVESTMENT":
                account = new InvestmentAccount(ownerCustomerId, branchCode, initialDeposit);
                break;
            default:
                System.err.println("Unknown account type: " + accountType);
                return null;
        }

        // Use reflection to set the account number and balance
        try {
            java.lang.reflect.Field accountNumberField = Account.class.getDeclaredField("accountNumber");
            accountNumberField.setAccessible(true);
            accountNumberField.set(account, accountNumber);

            java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(account, balance);

        } catch (Exception e) {
            System.err.println("Error setting account fields via reflection: " + e.getMessage());
        }

        return account;
    }

    // Additional methods for account operations
    public Account findAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAccountBalance(String accountNumber, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, accountNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AccountWithCustomer> findAllAccountsWithCustomerInfo() {
        List<AccountWithCustomer> accounts = new ArrayList<>();
        String sql = "SELECT a.*, c.first_name, c.last_name, c.national_id, c.phone_number, c.email " +
                "FROM accounts a " +
                "JOIN customers c ON a.owner_customer_id = c.customer_id " +
                "WHERE a.status = 'ACTIVE' " +
                "ORDER BY a.date_opened DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AccountWithCustomer accountWithCustomer = createAccountWithCustomerFromResultSet(rs);
                if (accountWithCustomer != null) {
                    accounts.add(accountWithCustomer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private AccountWithCustomer createAccountWithCustomerFromResultSet(ResultSet rs) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String ownerCustomerId = rs.getString("owner_customer_id");
        String branchCode = rs.getString("branch_code");
        BigDecimal balance = rs.getBigDecimal("balance");

        // Customer information
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String nationalId = rs.getString("national_id");
        String phoneNumber = rs.getString("phone_number");
        String email = rs.getString("email");

        String customerName = firstName + " " + lastName;

        return new AccountWithCustomer(
                accountNumber,
                customerName,
                accountType,
                balance,
                "Active", // Status
                nationalId,
                phoneNumber,
                email
        );
    }
}