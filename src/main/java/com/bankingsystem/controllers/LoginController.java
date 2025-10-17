package com.bankingsystem.controllers;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.Teller;
import com.bankingsystem.service.*;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

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

    private final com.bankingsystem.service.LoginService loginService = new com.bankingsystem.service.LoginService();

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            Object user = loginService.authenticate(username, password);

            if (user instanceof Teller teller) {
                SessionManager.setCurrentUser(teller, "TELLER");
                SceneNavigator.toTellerDashboard();

            } else if (user instanceof Customer customer) {
                SessionManager.setCurrentUser(customer, "CUSTOMER");
                SceneNavigator.toCustomerDashboard();

            } else {
                showError("Invalid credentials or role.");
            }

        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
