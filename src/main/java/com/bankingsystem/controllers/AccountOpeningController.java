package com.bankingsystem.controllers;

import com.bankingsystem.service.AccountService;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import com.bankingsystem.model.Teller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.math.BigDecimal;

public class AccountOpeningController {

    // Header
    @FXML private Text currentUserText;
    @FXML private Text progressText;

    // Step containers
    @FXML private VBox step1Personal;
    @FXML private VBox step2Contact;
    @FXML private VBox step3Account;
    @FXML private VBox step4Review;

    // Navigation buttons
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;

    // Personal Info
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> idTypeComboBox;
    @FXML private TextField idNumberField;
    @FXML private TextField nationalityField;

    // Contact Info
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressLine1Field;
    @FXML private TextField addressLine2Field;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField postalCodeField;

    // Account Info
    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField initialDepositField;
    @FXML private Label selectedAccountTypeLabel;
    @FXML private TextField reviewFirstName;
    @FXML private TextField reviewLastName;
    @FXML private TextField reviewIdNumber;
    @FXML private TextField reviewEmail;
    @FXML private TextField reviewPhone;
    @FXML private TextField reviewAccountType;
    @FXML private TextField reviewInitialDeposit;

    private int currentStep = 1;
    private AccountService accountService = new AccountService();
    private String preselectedAccountType;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupStepNavigation();
        loadPreselectedAccountType();
        showStep(1);
    }

    private void setupComboBoxes() {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        idTypeComboBox.setItems(FXCollections.observableArrayList("National ID (Omang)", "Passport", "Driver's License"));
        accountTypeComboBox.setItems(FXCollections.observableArrayList("Savings", "Investment", "Cheque"));
        nationalityField.setText("Botswana"); // Default for your bank
    }

    private void loadPreselectedAccountType() {
        // Get account type from session or previous selection
        Object accountType = SessionManager.getAttribute("selectedAccountType");
        if (accountType != null) {
            preselectedAccountType = accountType.toString();
            accountTypeComboBox.setValue(preselectedAccountType);
            selectedAccountTypeLabel.setText("Selected: " + preselectedAccountType);
        }

        // Set current user
        Object currentUser = SessionManager.getCurrentUser();
        if (currentUser instanceof Teller) {
            Teller teller = (Teller) currentUser;
            currentUserText.setText("Agent: " + teller.getFullName());
        }
    }

    private void setupStepNavigation() {
        backButton.setDisable(true);
        submitButton.setVisible(false);
    }

    private void showStep(int step) {
        // Hide all steps
        step1Personal.setVisible(false);
        step2Contact.setVisible(false);
        step3Account.setVisible(false);
        step4Review.setVisible(false);

        // Show current step
        switch (step) {
            case 1:
                step1Personal.setVisible(true);
                progressText.setText("Step 1: Personal Information");
                backButton.setDisable(true);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
                break;
            case 2:
                step2Contact.setVisible(true);
                progressText.setText("Step 2: Contact Information");
                backButton.setDisable(false);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
                break;
            case 3:
                step3Account.setVisible(true);
                progressText.setText("Step 3: Account Information");
                backButton.setDisable(false);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
                break;
            case 4:
                step4Review.setVisible(true);
                progressText.setText("Step 4: Review & Submit");
                backButton.setDisable(false);
                nextButton.setDisable(true);
                submitButton.setVisible(true);
                populateReviewSection();
                break;
        }
        currentStep = step;
    }

    private void populateReviewSection() {
        reviewFirstName.setText(firstNameField.getText());
        reviewLastName.setText(lastNameField.getText());
        reviewIdNumber.setText(idNumberField.getText());
        reviewEmail.setText(emailField.getText());
        reviewPhone.setText(phoneField.getText());
        reviewAccountType.setText(accountTypeComboBox.getValue());
        reviewInitialDeposit.setText(initialDepositField.getText());
    }

    // Navigation handlers
    @FXML
    private void handleNext(ActionEvent event) {
        if (validateCurrentStep()) {
            showStep(currentStep + 1);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        showStep(currentStep - 1);
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (validateForm()) {
            createAccountInDatabase();
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                return validatePersonalInfo();
            case 2:
                return validateContactInfo();
            case 3:
                return validateAccountInfo();
            default:
                return true;
        }
    }

    private boolean validatePersonalInfo() {
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "First name is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "Last name is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (idNumberField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "ID number is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            showAlert("Missing Information", "Date of birth is required.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean validateContactInfo() {
        if (emailField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "Email address is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "Phone number is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (addressLine1Field.getText().trim().isEmpty()) {
            showAlert("Missing Information", "Address is required.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean validateAccountInfo() {
        if (accountTypeComboBox.getValue() == null) {
            showAlert("Missing Information", "Account type is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (initialDepositField.getText().trim().isEmpty()) {
            showAlert("Missing Information", "Initial deposit is required.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double deposit = Double.parseDouble(initialDepositField.getText());
            if (deposit <= 0) {
                showAlert("Invalid Amount", "Initial deposit must be greater than 0.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number for initial deposit.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private boolean validateForm() {
        return validatePersonalInfo() && validateContactInfo() && validateAccountInfo();
    }

    private void createAccountInDatabase() {
        try {
            String tellerId = getCurrentTellerId();
            BigDecimal initialDeposit = new BigDecimal(initialDepositField.getText());

            // Build address
            String address = addressLine1Field.getText() + ", " +
                    (addressLine2Field.getText().isEmpty() ? "" : addressLine2Field.getText() + ", ") +
                    cityField.getText() + ", " + stateField.getText() + " " + postalCodeField.getText();

            String accountNumber = accountService.createNewAccount(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    idNumberField.getText().trim(),
                    dateOfBirthPicker.getValue(),
                    genderComboBox.getValue(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    address,
                    accountTypeComboBox.getValue(),
                    initialDeposit,
                    tellerId
            );

            if (accountNumber != null) {
                showAlert("Success",
                        "Account created successfully!\n\n" +
                                "Account Number: " + accountNumber + "\n" +
                                "Account Type: " + accountTypeComboBox.getValue() + "\n" +
                                "Customer: " + firstNameField.getText() + " " + lastNameField.getText(),
                        Alert.AlertType.INFORMATION);

                clearForm();
                showStep(1); // Return to first step
            } else {
                showAlert("Error", "Failed to create account. Please try again.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the account: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String getCurrentTellerId() {
        Object currentUser = SessionManager.getCurrentUser();
        if (currentUser instanceof Teller) {
            return ((Teller) currentUser).getTellerId();
        }
        return "UNKNOWN";
    }

    @FXML
    private void handleSaveDraft(ActionEvent event) {
        showAlert("Draft Saved", "The account application has been saved as a draft.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Application");
        alert.setHeaderText("Are you sure you want to cancel this application?");
        alert.setContentText("All entered data will be lost.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearForm();
                showStep(1);
            }
        });
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            SceneNavigator.toTellerDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleViewTerms(ActionEvent event) {
        showAlert("Terms and Conditions",
                "Sediba Financial Terms:\n- No overdraft allowed.\n- Minimum balance applies per account type.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleViewPrivacy(ActionEvent event) {
        showAlert("Privacy Policy",
                "Your data will be stored securely and used only for banking operations.",
                Alert.AlertType.INFORMATION);
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        dateOfBirthPicker.setValue(null);
        genderComboBox.setValue(null);
        idNumberField.clear();
        emailField.clear();
        phoneField.clear();
        addressLine1Field.clear();
        addressLine2Field.clear();
        cityField.clear();
        stateField.clear();
        postalCodeField.clear();
        initialDepositField.clear();

        // Reset account type but keep preselected if any
        if (preselectedAccountType != null) {
            accountTypeComboBox.setValue(preselectedAccountType);
        } else {
            accountTypeComboBox.setValue(null);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}