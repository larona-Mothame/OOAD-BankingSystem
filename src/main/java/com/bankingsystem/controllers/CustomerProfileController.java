package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

public class CustomerProfileController {

    // Header
    @FXML private Text usernameHeaderText;
    @FXML private Text currentUserText;

    // Lookup
    @FXML private TextField searchField;

    // Customer info fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField idNumberField;
    @FXML private TextField accountCountField;
    @FXML private TextField otherInfoField;

    // =============================
    // Initialization
    // =============================
    @FXML
    public void initialize() {
        usernameHeaderText.setText("Teller01");
        currentUserText.setText("Logged in as Teller01");
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    // =============================
    // Handlers
    // =============================
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showAlert(AlertType.WARNING, "Empty Search", "Please enter a name, account number, or ID to search.");
            return;
        }

        // TODO: Integrate with DAO
        if (query.equalsIgnoreCase("Larona")) {
            firstNameField.setText("Larona");
            lastNameField.setText("Mothame");
            genderComboBox.setValue("Male");
            idNumberField.setText("123456789");
            accountCountField.setText("3");
            otherInfoField.setText("Preferred customer");
        } else {
            showAlert(AlertType.INFORMATION, "No Results", "No customer found for: " + query);
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Profile Updated", "Customer information has been updated successfully.");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Incomplete Data", "First and last names cannot be empty.");
            return;
        }
        showAlert(AlertType.INFORMATION, "Saved", "Customer profile has been saved successfully.");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Logout", "You have logged out successfully.");
        // TODO: Add scene navigation to Login.fxml
    }

    @FXML
    private void handleViewAllCustomers(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "All Customers", "Displaying all registered customers.");
    }

    @FXML
    private void handleTermsAndConditions(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Terms and Conditions",
                "Sediba Financial follows strict compliance and KYC procedures as per banking policy.");
    }

    // =============================
    // Utility
    // =============================
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
