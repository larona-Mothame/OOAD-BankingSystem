package com.bankingsystem.controllers;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.Teller;
import com.bankingsystem.service.LoginService; // Cleaned up import
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Handles user login for both Teller and Customer roles.
 */
public class LoginController {

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private ImageView eyeIcon;


    @FXML
    private TextField usernameField;

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
        setupEnterKeySupport();
        setupEyeToggle();
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


    private boolean passwordVisibleFlag = false;

    private void setupEyeToggle() {
        // Keep passwordField and passwordVisible in sync
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());

        // Load eye icons
        Image openEye = new Image(getClass().getResourceAsStream("/images/eye-open.png"));
        Image closedEye = new Image(getClass().getResourceAsStream("/images/eye-closed.png"));

        // Set initial icon
        eyeIcon.setImage(closedEye);

        togglePasswordBtn.setOnAction(e -> {
            passwordVisibleFlag = !passwordVisibleFlag;

            if (passwordVisibleFlag) {
                passwordVisible.setVisible(true);
                passwordVisible.setManaged(true);

                passwordField.setVisible(false);
                passwordField.setManaged(false);

                eyeIcon.setImage(openEye);
            } else {
                passwordVisible.setVisible(false);
                passwordVisible.setManaged(false);

                passwordField.setVisible(true);
                passwordField.setManaged(true);

                eyeIcon.setImage(closedEye);
            }
        });
    }




}