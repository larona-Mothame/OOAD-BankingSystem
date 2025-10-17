package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.collections.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

public class TransactionController {

    // Header
    @FXML private Text usernameText;

    // Lookup
    @FXML private TextField accountSearchField;

    // Account summary
    @FXML private Text accountHolderText;
    @FXML private Text accountNumberText;
    @FXML private Text balanceText;

    // Transaction inputs
    @FXML private ComboBox<String> transactionTypeCombo;
    @FXML private TextField amountField;
    @FXML private TextField referenceField;

    // Transaction table
    @FXML private TableView<?> transactionTable;

    private double balance = 0.0;

    @FXML
    public void initialize() {
        usernameText.setText("Teller01");
        transactionTypeCombo.setItems(FXCollections.observableArrayList("Deposit", "Withdrawal"));
    }

    @FXML
    private void handleSearchAccount(ActionEvent event) {
        String accountNum = accountSearchField.getText().trim();

        if (accountNum.isEmpty()) {
            showAlert(AlertType.WARNING, "Empty Field", "Please enter an account number.");
            return;
        }

        // Simulate lookup
        if (accountNum.equals("1001")) {
            accountHolderText.setText("Larona Mothame");
            accountNumberText.setText("1001");
            balance = 8500.75;
            balanceText.setText(String.format("%.2f", balance));
        } else {
            showAlert(AlertType.INFORMATION, "Not Found", "No account found for " + accountNum);
        }
    }

    @FXML
    private void handleConfirmTransaction(ActionEvent event) {
        String type = transactionTypeCombo.getValue();
        String amountStr = amountField.getText().trim();

        if (type == null || amountStr.isEmpty()) {
            showAlert(AlertType.WARNING, "Incomplete Details", "Select a transaction type and enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(AlertType.ERROR, "Invalid Amount", "Enter a valid positive amount.");
                return;
            }

            if (type.equals("Deposit")) {
                balance += amount;
            } else if (type.equals("Withdrawal")) {
                if (amount > balance) {
                    showAlert(AlertType.ERROR, "Insufficient Funds", "Cannot withdraw more than the current balance.");
                    return;
                }
                balance -= amount;
            }

            balanceText.setText(String.format("%.2f", balance));
            showAlert(AlertType.INFORMATION, "Transaction Successful",
                    String.format("%s of $%.2f completed successfully.", type, amount));

            // TODO: log in DB + add to table

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Amount must be a numeric value.");
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        amountField.clear();
        referenceField.clear();
        transactionTypeCombo.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Logout", "You have been logged out.");
    }

    @FXML
    private void handleTermsAndConditions(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Terms & Conditions", "Transactions are subject to Sediba Financial policy.");
    }

    // Utility
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
