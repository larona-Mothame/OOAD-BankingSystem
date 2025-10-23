package com.bankingsystem.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;


public class AccountLookupController {

    // Header
    @FXML private Text usernameText;

    // Search & filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<AccountResult> resultsTable;

    // Account detail fields
    @FXML private Text accountNumberText;
    @FXML private Text accountHolderText;
    @FXML private Text accountTypeText;
    @FXML private Text dateCreatedText;

    // =========================
    // Initialization
    // =========================
    @FXML
    public void initialize() {
        usernameText.setText("Teller01");
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Savings", "Cheque", "Investment"));
        filterComboBox.getSelectionModel().selectFirst();

        // Initialize columns dynamically if not defined in FXML
        if (resultsTable.getColumns().isEmpty()) {
            TableColumn<AccountResult, String> accNumCol = new TableColumn<>("Account Number");
            accNumCol.setCellValueFactory(c -> c.getValue().accountNumberProperty());

            TableColumn<AccountResult, String> holderCol = new TableColumn<>("Account Holder");
            holderCol.setCellValueFactory(c -> c.getValue().accountHolderProperty());

            TableColumn<AccountResult, String> typeCol = new TableColumn<>("Account Type");
            typeCol.setCellValueFactory(c -> c.getValue().accountTypeProperty());

            TableColumn<AccountResult, String> dateCol = new TableColumn<>("Date Created");
            dateCol.setCellValueFactory(c -> c.getValue().dateCreatedProperty());

            resultsTable.getColumns().addAll(accNumCol, holderCol, typeCol, dateCol);
        }
    }

    // =========================
    // Search & Actions
    // =========================
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        String filter = filterComboBox.getValue();

        if (query.isEmpty()) {
            showAlert(AlertType.WARNING, "Empty Search", "Please enter a keyword or account number to search.");
            return;
        }

        // Mock search results (replace with real DB call later)
        ObservableList<AccountResult> results = FXCollections.observableArrayList(
                new AccountResult("ACC-001", "Larona Mothame", "Savings", "2023-09-12"),
                new AccountResult("ACC-002", "John Doe", "Cheque", "2024-01-18")
        );

        resultsTable.setItems(results);
        resultsTable.setVisible(true);
        resultsTable.setManaged(true);
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        if (!validateSelection()) return;
        showAlert(AlertType.INFORMATION, "Profile", "Opening full customer profile for selected account.");
    }

    @FXML
    private void handleViewTransactionHistory(ActionEvent event) {
        if (!validateSelection()) return;
        showAlert(AlertType.INFORMATION, "Transactions", "Displaying transaction history for selected account.");
    }

    @FXML
    private void handleEditAccount(ActionEvent event) {
        if (!validateSelection()) return;
        showAlert(AlertType.INFORMATION, "Edit Account", "Navigating to account edit view.");
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Dashboard", "Returning to main teller dashboard.");
        // TODO: Implement scene switch back to TellerDashboard.fxml
    }

    // =========================
    // Utility
    // =========================
    private boolean validateSelection() {
        AccountResult selected = resultsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select an account from the table first.");
            return false;
        }

        // Populate account detail section
        accountNumberText.setText(selected.getAccountNumber());
        accountHolderText.setText(selected.getAccountHolder());
        accountTypeText.setText(selected.getAccountType());
        dateCreatedText.setText(selected.getDateCreated());
        return true;
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleViewFullProfile(ActionEvent actionEvent) {
    }

    public void handleTransactionHistory(ActionEvent actionEvent) {
    }


    public static class AccountResult {
        private final StringProperty accountNumber;
        private final StringProperty accountHolder;
        private final StringProperty accountType;
        private final StringProperty dateCreated;

        public AccountResult(String accountNumber, String accountHolder, String accountType, String dateCreated) {
            this.accountNumber = new SimpleStringProperty(accountNumber);
            this.accountHolder = new SimpleStringProperty(accountHolder);
            this.accountType = new SimpleStringProperty(accountType);
            this.dateCreated = new SimpleStringProperty(dateCreated);
        }

        public String getAccountNumber() { return accountNumber.get(); }
        public String getAccountHolder() { return accountHolder.get(); }
        public String getAccountType() { return accountType.get(); }
        public String getDateCreated() { return dateCreated.get(); }

        public StringProperty accountNumberProperty() { return accountNumber; }
        public StringProperty accountHolderProperty() { return accountHolder; }
        public StringProperty accountTypeProperty() { return accountType; }
        public StringProperty dateCreatedProperty() { return dateCreated; }
    }
}
