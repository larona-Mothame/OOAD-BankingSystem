package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public class CustomerDashboardController {

    // --- Header ---
    @FXML private Text customerNameText;
    @FXML private Label totalBalanceLabel;

    // --- Quick Stats ---
    @FXML private Text totalAccountsText;
    @FXML private Text monthlySpendingText;
    @FXML private Text lastTransactionText;

    // --- Accounts List ---
    @FXML private VBox accountsContainer;

    // --- Transaction Panel ---
    @FXML private VBox transactionPanel;
    @FXML private VBox welcomePanel;
    @FXML private VBox transactionForm;
    @FXML private Text transactionTitle;
    @FXML private Text selectedAccountText;
    @FXML private Text selectedBalanceText;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> transferAccountComboBox;
    @FXML private VBox transferSection;

    // --- Transactions Table ---
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private ComboBox<String> transactionFilterComboBox;

    // ==============================
    // Initialization
    // ==============================
    @FXML
    public void initialize() {
        // Placeholder setup for demonstration
        customerNameText.setText("Larona Mothame");
        totalBalanceLabel.setText("Total: BWP 12,450.00");
        totalAccountsText.setText("3");
        monthlySpendingText.setText("BWP 1,200.00");
        lastTransactionText.setText("Withdrawal - BWP 200.00");

        // Populate transaction table with demo data
        loadMockTransactions();

        // Populate filter combo
        transactionFilterComboBox.setItems(FXCollections.observableArrayList("All Accounts", "Savings", "Investment", "Cheque"));
        transactionFilterComboBox.getSelectionModel().selectFirst();
    }

    private void loadMockTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList(
                new Transaction("2025-10-10", "Deposit", "Savings", "BWP 500.00", "BWP 5,500.00"),
                new Transaction("2025-10-11", "Withdraw", "Cheque", "BWP 200.00", "BWP 1,800.00"),
                new Transaction("2025-10-14", "Interest Added", "Investment", "BWP 250.00", "BWP 7,350.00")
        );
        transactionsTable.setItems(transactions);
    }

    // ==============================
    // Button Handlers
    // ==============================

    @FXML
    private void handleLogout() {
        showAlert(AlertType.INFORMATION, "Logout", "You have been logged out successfully.");
        // TODO: Redirect to login view
    }

    @FXML
    private void handleTransfer() {
        transactionTitle.setText("Transfer Funds");
        transferSection.setVisible(true);
        openTransactionPanel();
    }

    @FXML
    private void handlePayBills() {
        transactionTitle.setText("Pay Bills");
        transferSection.setVisible(false);
        openTransactionPanel();
    }

    @FXML
    private void handleAccountSettings() {
        showAlert(AlertType.INFORMATION, "Account Settings", "Account settings panel under development.");
    }

    @FXML
    private void handleContactSupport() {
        showAlert(AlertType.INFORMATION, "Contact Support", "Support contact feature coming soon.");
    }

    @FXML
    private void handleCancelTransaction() {
        closeTransactionPanel();
    }

    @FXML
    private void handleConfirmTransaction() {
        String amount = amountField.getText().trim();
        if (amount.isEmpty()) {
            showAlert(AlertType.WARNING, "Invalid Input", "Please enter a valid transaction amount.");
            return;
        }
        showAlert(AlertType.INFORMATION, "Transaction Complete", "Transaction of " + amount + " was processed successfully.");
        closeTransactionPanel();
    }

    @FXML
    private void handleViewAllTransactions() {
        showAlert(AlertType.INFORMATION, "Transactions", "Showing all recent transactions.");
    }

    @FXML
    private void handleTermsAndConditions() {
        showAlert(AlertType.INFORMATION, "Terms and Conditions", "All operations are governed by Sediba Financial policies.");
    }

    @FXML
    private void handleCloseTransaction() {
        closeTransactionPanel();
    }

    // ==============================
    // Utility methods
    // ==============================

    private void openTransactionPanel() {
        welcomePanel.setVisible(false);
        transactionPanel.setVisible(true);
    }

    private void closeTransactionPanel() {
        transactionPanel.setVisible(false);
        welcomePanel.setVisible(true);
        clearTransactionForm();
    }

    private void clearTransactionForm() {
        amountField.clear();
        descriptionField.clear();
        transferAccountComboBox.getSelectionModel().clearSelection();
        transferSection.setVisible(false);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==============================
    // Inner Class (Mock Transaction)
    // ==============================
    public static class Transaction {
        private final String date;
        private final String description;
        private final String account;
        private final String amount;
        private final String balance;

        public Transaction(String date, String description, String account, String amount, String balance) {
            this.date = date;
            this.description = description;
            this.account = account;
            this.amount = amount;
            this.balance = balance;
        }

        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getAccount() { return account; }
        public String getAmount() { return amount; }
        public String getBalance() { return balance; }
    }
}
