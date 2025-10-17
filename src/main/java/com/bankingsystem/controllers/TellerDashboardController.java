package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TellerDashboardController {

    @FXML
    private Text usernameText;

    @FXML
    public void initialize() {
        // Example: set username dynamically, in a real app replace with logged-in user
        usernameText.setText("Admin");
    }

    // -------------------
    // Account card clicks
    // -------------------
    @FXML
    private void openChequingAccount(MouseEvent event) {
        showInfo("Chequing Account", "Navigate to Chequing Account creation/manage screen.");
    }

    @FXML
    private void openSavingsAccount(MouseEvent event) {
        showInfo("Savings Account", "Navigate to Savings Account creation/manage screen.");
    }

    @FXML
    private void openInvestmentAccount(MouseEvent event) {
        showInfo("Investment Account", "Navigate to Investment Account creation/manage screen.");
    }

    // -------------------
    // Customer action card clicks
    // -------------------
    @FXML
    private void proceedCustomer(MouseEvent event) {
        showInfo("Proceed", "Continue customer operations screen.");
    }

    @FXML
    private void manageCustomer(MouseEvent event) {
        showInfo("Manage", "Navigate to Customer account management screen.");
    }

    // -------------------
    // Utility method for alerts
    // -------------------
    private void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
