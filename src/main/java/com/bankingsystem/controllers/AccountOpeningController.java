package com.bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import java.time.LocalDate;

public class AccountOpeningController {

    // Header
    @FXML private Text currentUserText;

    // Personal Info
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> idTypeComboBox;
    @FXML private TextField idNumberField;
    @FXML private TextField nationalityField;
    @FXML private ComboBox<String> maritalStatusComboBox;

    // Contact Info
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressLine1Field;
    @FXML private TextField addressLine2Field;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> countryComboBox;

    // Employment Info
    @FXML private ComboBox<String> employmentStatusComboBox;
    @FXML private TextField occupationField;
    @FXML private TextField employerField;
    @FXML private TextField incomeField;

    // Account Info
    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField initialDepositField;
    @FXML private CheckBox debitCardCheck;
    @FXML private CheckBox onlineBankingCheck;
    @FXML private CheckBox mobileBankingCheck;
    @FXML private CheckBox overdraftProtectionCheck;

    // Terms
    @FXML private CheckBox termsAcceptCheck;
    @FXML private CheckBox privacyAcceptCheck;

    // ==============================
    // Initialization
    // ==============================
    @FXML
    public void initialize() {
        currentUserText.setText("Agent: Teller01");

        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        idTypeComboBox.setItems(FXCollections.observableArrayList("National ID (Omang)", "Passport", "Driver’s License"));
        maritalStatusComboBox.setItems(FXCollections.observableArrayList("Single", "Married", "Divorced", "Widowed"));
        countryComboBox.setItems(FXCollections.observableArrayList("Botswana", "South Africa", "Namibia", "Zimbabwe"));
        employmentStatusComboBox.setItems(FXCollections.observableArrayList("Employed", "Self-Employed", "Unemployed", "Retired"));
        accountTypeComboBox.setItems(FXCollections.observableArrayList("Savings Account", "Investment Account", "Cheque Account"));
    }

    // ==============================
    // Button Handlers
    // ==============================
    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateForm()) return;

        // Mock save
        String accountType = accountTypeComboBox.getValue();
        String accountNumber = generateAccountNumber(accountType);

        showAlert(AlertType.INFORMATION, "Application Submitted",
                "Account created successfully!\n\nAccount Number: " + accountNumber +
                        "\nAccount Type: " + accountType +
                        "\nCustomer: " + firstNameField.getText() + " " + lastNameField.getText());

        clearForm();
    }

    @FXML
    private void handleSaveDraft(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Draft Saved", "The account application has been saved as a draft.");
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        showAlert(AlertType.CONFIRMATION, "Cancel Application", "Are you sure you want to cancel this application?");
        clearForm();
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Navigation", "Returning to Teller Dashboard...");
        // TODO: Replace with scene switch logic
    }

    @FXML
    private void handleViewTerms(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Terms and Conditions",
                "Sediba Financial Terms:\n- No overdraft allowed.\n- Minimum balance applies per account type.");
    }

    @FXML
    private void handleViewPrivacy(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Privacy Policy",
                "Your data will be stored securely and used only for banking operations.");
    }

    @FXML
    private void handleTermsAndConditions(ActionEvent event) {
        handleViewTerms(event);
    }

    // ==============================
    // Validation & Utility Methods
    // ==============================
    private boolean validateForm() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                idNumberField.getText().isEmpty() || accountTypeComboBox.getValue() == null ||
                initialDepositField.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Missing Fields", "Please fill in all required fields before submitting.");
            return false;
        }

        if (!termsAcceptCheck.isSelected() || !privacyAcceptCheck.isSelected()) {
            showAlert(AlertType.WARNING, "Agreement Required", "Please accept the Terms and Privacy Policy to proceed.");
            return false;
        }

        // Validate initial deposit based on account type
        try {
            double deposit = Double.parseDouble(initialDepositField.getText());
            String type = accountTypeComboBox.getValue();

            if (type.equals("Investment Account") && deposit < 500) {
                showAlert(AlertType.WARNING, "Minimum Deposit Required",
                        "Investment accounts require a minimum deposit of BWP 500.");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Invalid Amount", "Please enter a valid numeric value for initial deposit.");
            return false;
        }

        return true;
    }

    private String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType) {
            case "Savings Account": prefix = "SAV"; break;
            case "Investment Account": prefix = "INV"; break;
            case "Cheque Account": prefix = "CHQ"; break;
            default: prefix = "GEN";
        }
        int randomNum = (int) (Math.random() * 9000 + 1000);
        return prefix + "-" + randomNum;
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        idNumberField.clear();
        emailField.clear();
        phoneField.clear();
        addressLine1Field.clear();
        addressLine2Field.clear();
        cityField.clear();
        stateField.clear();
        postalCodeField.clear();
        nationalityField.clear();
        occupationField.clear();
        employerField.clear();
        incomeField.clear();
        initialDepositField.clear();
        termsAcceptCheck.setSelected(false);
        privacyAcceptCheck.setSelected(false);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
