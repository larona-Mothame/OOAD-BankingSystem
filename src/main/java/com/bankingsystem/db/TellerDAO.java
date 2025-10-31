package com.bankingsystem.db;

import com.bankingsystem.database.DBConnection;
import com.bankingsystem.model.Teller;
import com.bankingsystem.util.PasswordUtil;

import java.sql.*;

public class TellerDAO {

    public Teller findByUsername(String username) {
        String sql = "SELECT * FROM tellers WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createTellerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teller findByUsernameAndPassword(String username, String password) {
        System.out.println("DEBUG: Authenticating teller: " + username);
        Teller teller = findByUsername(username);

        if (teller != null) {
            System.out.println("DEBUG: Found teller: " + teller.getFullName());
            System.out.println("DEBUG: Stored hash: " + teller.getPasswordHash());
            System.out.println("DEBUG: Input password: " + password);
            boolean passwordMatch = verifyPassword(password, teller.getPasswordHash());
            System.out.println("DEBUG: Password match: " + passwordMatch);

            if (passwordMatch && teller.isActive()) {
                return teller;
            }
        } else {
            System.out.println("DEBUG: No teller found with username: " + username);
        }

        return null;
    }

    private Teller createTellerFromResultSet(ResultSet rs) throws SQLException {
        System.out.println("DEBUG: Creating teller from ResultSet...");

        // Debug: Print all columns to see what we're getting
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        System.out.println("DEBUG: Available columns:");
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String columnValue = rs.getString(i);
            System.out.println("  " + columnName + ": " + columnValue);
        }

        // Create teller with correct column mapping
        Teller teller = new Teller(
                rs.getString("teller_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password_hash"),  // Make sure this matches your DB column name!
                rs.getBoolean("is_active")
        );

        System.out.println("DEBUG: Created teller - " + teller.getFullName());
        System.out.println("DEBUG: Teller password hash: " + teller.getPasswordHash());

        return teller;
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        return PasswordUtil.verifyPassword(inputPassword, storedHash);
    }
}