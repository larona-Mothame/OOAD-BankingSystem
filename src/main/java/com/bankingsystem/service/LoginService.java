package com.bankingsystem.service;

import com.bankingsystem.database.DBConnection;
import com.bankingsystem.model.Customer;
import com.bankingsystem.model.IndividualCustomer;
import com.bankingsystem.model.Teller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles authentication logic for both Tellers and Customers.
 * Communicates directly with the database to verify credentials.
 */
public class LoginService {

    /**
     * Authenticates a user (either Teller or Customer) by username and password.
     *
     * @param username the username provided in the login form
     * @param password the password provided in the login form
     * @return Teller or Customer object if authentication succeeds; null otherwise
     * @throws SQLException if a database access error occurs
     */
    public Object authenticate(String username, String password) throws SQLException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be empty.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ Attempt Teller login
            String tellerQuery = "SELECT * FROM tellers WHERE username = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(tellerQuery)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Teller teller = new Teller();
                    teller.setTellerId(rs.getString("teller_id"));
                    teller.setFirstName(rs.getString("first_name"));
                    teller.setLastName(rs.getString("last_name"));
                    teller.setUsername(rs.getString("username"));
                    // You can store password optionally if needed for session
                    return teller;
                }
            }

            // 2️⃣ Attempt Customer login
            String customerQuery = "SELECT * FROM customers WHERE username = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(customerQuery)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    // Combine first/last name if separate in DB, else use "name" column
                    String fullName;
                    try {
                        fullName = rs.getString("name"); // preferred field
                    } catch (SQLException e) {
                        fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    }

                    String contact = rs.getString("contact_number");
                    String email = rs.getString("email");
                    String address = rs.getString("address");

                    // Use IndividualCustomer or CompanyCustomer depending on your app logic
                    Customer customer = new IndividualCustomer(fullName, contact, email, address);
                    customer.setUsername(rs.getString("username"));
                    customer.setPassword(rs.getString("password"));

                    return customer;
                }
            }

            // 3️⃣ No match found
            return null;
        }
    }
}
