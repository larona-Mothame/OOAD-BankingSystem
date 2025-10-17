package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;

public class TransactionHistoryController {

    @FXML private Text usernameText;

    @FXML private TextField accountNumberField;
    @FXML private ComboBox<String> transactionTypeCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TableView<?> transactionTable;

    @FXML
    public void initialize() {
        usernameText.setText("Teller01");
        transactionTypeCombo.setItems(FXCollections.observableArrayList("All", "Deposit", "Withdrawal"));
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String accountNum = accountNumberField.getText().trim();
        String type = transactionTypeCombo.getValue();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        if (accountNum.isEmpty() && (type == null || type.equals("All")) && from == null && to == null) {
            showAlert(AlertType.WARNING, "No Filters", "Please specify at least one filter to search transactions.");
            return;
        }

        // TODO: Query from TransactionDAO (filter by account, type, date)
        showAlert(AlertType.INFORMATION, "Search Results", "Displaying results for applied filters.");
    }

    @FXML
    private void handleExportCSV(ActionEvent event) {
        // TODO: Implement CSV export logic
        showAlert(AlertType.INFORMATION, "Export Complete", "Transactions have been exported to CSV successfully.");
    }

    @FXML
    private void handlePrintReport(ActionEvent event) {
        // TODO: Implement report printing logic
        showAlert(AlertType.INFORMATION, "Print", "Transaction report sent to printer.");
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        accountNumberField.clear();
        transactionTypeCombo.getSelectionModel().clearSelection();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Navigation", "Returning to dashboard...");
        // TODO: Implement navigation logic
    }

    @FXML
    private void handleTermsAndConditions(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Terms & Conditions", "All transactions follow Sediba Financial policy.");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
