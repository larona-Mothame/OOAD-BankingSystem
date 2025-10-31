package com.bankingsystem.controllers;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.Teller;
import com.bankingsystem.service.LoginService; // Cleaned up import
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Handles user login for both Teller and Customer roles.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Text bankName;

    @FXML
    private Label errorLabel;

    private final LoginService loginService = new LoginService();

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);

        // Optional: Add Enter key support for login
        setupEnterKeySupport();
    }

    /**
     * Allow login by pressing Enter key
     */
    private void setupEnterKeySupport() {
        usernameField.setOnAction(this::handleLogin);
        passwordField.setOnAction(this::handleLogin);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Clear previous errors
        clearError();

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (!validateInputs(username, password)) {
            return;
        }

        // Disable login button during authentication to prevent multiple attempts
        loginButton.setDisable(true);

        try {
            Object user = loginService.authenticate(username, password);

            if (user instanceof Teller teller) {
                handleTellerLogin(teller);
            } else if (user instanceof Customer customer) {
                handleCustomerLogin(customer);
            } else {
                showError("Invalid username or password. Please try again.");
            }

        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace(); // Helpful for debugging
        } finally {
            // Re-enable login button regardless of outcome
            loginButton.setDisable(false);
        }
    }

    /**
     * Validate user inputs
     */
    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() && password.isEmpty()) {
            showError("Please enter username and password.");
            return false;
        } else if (username.isEmpty()) {
            showError("Please enter your username.");
            return false;
        } else if (password.isEmpty()) {
            showError("Please enter your password.");
            return false;
        }
        return true;
    }

    /**
     * Handle successful teller login
     */
    private void handleTellerLogin(Teller teller) throws IOException {
        if (!teller.canLogin()) {
            showError("Teller account is inactive. Please contact administrator.");
            return;
        }

        SessionManager.setCurrentUser(teller, "TELLER");
        showSuccessMessage("Welcome back, " + teller.getFullName() + "!");
        SceneNavigator.toTellerDashboard();
    }

    /**
     * Handle successful customer login
     */
    private void handleCustomerLogin(Customer customer) throws IOException {
        if (!customer.canLogin()) {
            showError("Customer account is inactive. Please contact support.");
            return;
        }

        SessionManager.setCurrentUser(customer, "CUSTOMER");
        showSuccessMessage("Welcome back, " + customer.getDisplayName() + "!");
        SceneNavigator.toCustomerDashboard();
    }

    /**
     * Show error message to user
     */
    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        // Clear error after 5 seconds
        clearErrorAfterDelay();
    }

    /**
     * Show success message (optional)
     */
    private void showSuccessMessage(String message) {
        // You could implement a success message display here
        System.out.println("Login successful: " + message);
    }

    /**
     * Clear error message
     */
    private void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setText("");
    }

    /**
     * Clear error message after delay
     */
    private void clearErrorAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(5000); // 5 seconds
                javafx.application.Platform.runLater(this::clearError);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Optional: Clear fields method
     */
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        clearError();
    }
}