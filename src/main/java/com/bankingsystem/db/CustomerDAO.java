package com.bankingsystem.db;
import com.bankingsystem.model.CompanyCustomer;
import com.bankingsystem.model.Customer;
import com.bankingsystem.model.IndividualCustomer;
import com.bankingsystem.util.PasswordUtil;
import com.bankingsystem.database.DBConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerDAO {

    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());

    public Customer findByUsername(String username) {
        LOGGER.info("Searching for customer with username: " + username);
        String sql = "SELECT * FROM customers WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createCustomerFromResultSet(rs);
            } else {
                LOGGER.info("No customer found with username: " + username);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error while finding customer by username: " + username, e);
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

        // 👉 Detect if this is a company
        if (isCompanyCustomer(customerId)) {
            return loadCompanyCustomer(
                    customerId,
                    firstName,  // stored as company name
                    phoneNumber,
                    email,
                    address,
                    username,
                    passwordHash,
                    isActive
            );
        }

        // Otherwise load individual
        return new IndividualCustomer(
                customerId,
                firstName + " " + lastName,
                phoneNumber,
                email,
                address,
                username,
                passwordHash,
                isActive,
                nationalId,
                dateOfBirth
        );
    }


    private boolean verifyPassword(String inputPassword, String storedHash) {
        return PasswordUtil.verifyPassword(inputPassword, storedHash);
    }

    public boolean updateCustomer(String nationalId, String firstName, String lastName, String contactInfo) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, phone_number = ?, email = ? WHERE national_id = ?";

        // Parse contactInfo into phone and email if needed
        String phone = "";
        String email = "";
        if (contactInfo != null && !contactInfo.isEmpty()) {
            String[] parts = contactInfo.split("\\|");
            phone = parts.length > 0 ? parts[0].trim() : "";
            email = parts.length > 1 ? parts[1].trim() : "";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, nationalId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update customer with national ID: " + nationalId, e);
            return false;
        }
    }

    private boolean isCompanyCustomer(String customerId) {
        String sql = "SELECT 1 FROM company_customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check company customer", e);
            return false;
        }
    }

    private Customer loadCompanyCustomer(String customerId,
                                         String companyName,
                                         String contactNumber,
                                         String email,
                                         String address,
                                         String username,
                                         String passwordHash,
                                         boolean isActive) {

        String sql = "SELECT * FROM company_customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String regNo = rs.getString("company_registration_number");
                String primaryContact = rs.getString("primary_contact_person");

                return new CompanyCustomer(
                        customerId,
                        companyName,         // stored as first_name
                        contactNumber,
                        email,
                        address,
                        username,
                        passwordHash,
                        isActive,
                        regNo,
                        primaryContact
                );
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed loading company details", ex);
        }

        // fallback (shouldn't happen)
        return new CompanyCustomer(
                customerId,
                companyName,
                contactNumber,
                email,
                address,
                username,
                passwordHash,
                isActive,
                "UNKNOWN",
                null
        );
    }


}
