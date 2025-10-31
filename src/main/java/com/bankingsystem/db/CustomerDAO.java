package com.bankingsystem.db;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.IndividualCustomer;
import com.bankingsystem.util.PasswordUtil;
import com.bankingsystem.database.DBConnection;

import java.sql.*;

public class CustomerDAO {

    public Customer findByUsername(String username) {
        System.out.println("CustomerDAO: Searching for customer with username: " + username);

        String sql = "SELECT * FROM customers WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("CustomerDAO: Customer found, creating customer object...");
                return createCustomerFromResultSet(rs);
            } else {
                System.out.println("CustomerDAO: No customer found with username: " + username);
            }
        } catch (SQLException e) {
            System.out.println("CustomerDAO: SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Customer findByUsernameAndPassword(String username, String password) {
        Customer customer = findByUsername(username);
        if (customer != null && verifyPassword(password, customer.getPasswordHash())) {
            return customer;
        }
        return null;
    }

    private Customer createCustomerFromResultSet(ResultSet rs) throws SQLException {
        System.out.println("DEBUG: Creating customer from ResultSet...");

        // Debug: Print all available columns
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        System.out.println("DEBUG: Available columns in customers table:");
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            try {
                String columnValue = rs.getString(i);
                System.out.println("  " + columnName + ": " + columnValue);
            } catch (SQLException e) {
                System.out.println("  " + columnName + ": [Cannot read value]");
            }
        }

        try {
            // Get all columns using your exact schema
            String customerId = rs.getString("customer_id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            String email = rs.getString("email");
            String phoneNumber = rs.getString("phone_number");
            String address = rs.getString("address");
            boolean isActive = rs.getBoolean("is_active");
            String nationalId = rs.getString("national_id");
            Date dateOfBirth = rs.getDate("date_of_birth");

            System.out.println("DEBUG: Retrieved values - Name: " + firstName + " " + lastName + ", Active: " + isActive);

            // Create IndividualCustomer with all the data
            IndividualCustomer customer = new IndividualCustomer(
                    customerId,
                    firstName + " " + lastName,  // name
                    phoneNumber,                 // contactNumber
                    email,                       // email
                    address,                     // address
                    username,
                    passwordHash,
                    isActive,
                    nationalId,
                    dateOfBirth
            );

            System.out.println("DEBUG: Successfully created customer: " + customer.getDisplayName());
            return customer;

        } catch (SQLException e) {
            System.out.println("ERROR: Failed to create customer from ResultSet: " + e.getMessage());
            System.out.println("ERROR: Specific column causing issue might be: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to see the exact line
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        return PasswordUtil.verifyPassword(inputPassword, storedHash);
    }
}