package com.bankingsystem.controllers;

import com.bankingsystem.service.AccountService;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import com.bankingsystem.model.Teller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

public class AccountOpeningController {

    @FXML private Text progressText;

    @FXML private Label stepLabel1;
    @FXML private Label stepLabel2;
    @FXML private Label stepLabel3;
    @FXML private Label stepLabel4;

    @FXML private VBox step1Personal;
    @FXML private VBox step2Contact;
    @FXML private VBox step3Account;
    @FXML private VBox step4Review;

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
        nationalityField.setText("Botswana");
    }

    private void loadPreselectedAccountType() {
        Object accountType = SessionManager.getAttribute("selectedAccountType");
        if (accountType != null) {
            preselectedAccountType = accountType.toString();
            accountTypeComboBox.setValue(preselectedAccountType);
            selectedAccountTypeLabel.setText("Selected: " + preselectedAccountType);
        }


    }

    private void setupStepNavigation() {
        backButton.setDisable(true);
        submitButton.setVisible(false);
    }

    private void showStep(int step) {
        step1Personal.setVisible(false); step1Personal.setManaged(false);
        step2Contact.setVisible(false); step2Contact.setManaged(false);
        step3Account.setVisible(false); step3Account.setManaged(false);
        step4Review.setVisible(false); step4Review.setManaged(false);

        switch (step) {
            case 1 -> {
                step1Personal.setVisible(true); step1Personal.setManaged(true);
                progressText.setText("Step 1: Personal Information");
                backButton.setDisable(true); nextButton.setDisable(false); submitButton.setVisible(false);
            }
            case 2 -> {
                step2Contact.setVisible(true); step2Contact.setManaged(true);
                progressText.setText("Step 2: Contact Information");
                backButton.setDisable(false); nextButton.setDisable(false); submitButton.setVisible(false);
            }
            case 3 -> {
                step3Account.setVisible(true); step3Account.setManaged(true);
                progressText.setText("Step 3: Account Information");
                backButton.setDisable(false); nextButton.setDisable(false); submitButton.setVisible(false);
            }
            case 4 -> {
                step4Review.setVisible(true); step4Review.setManaged(true);
                progressText.setText("Step 4: Review & Submit");
                backButton.setDisable(false); nextButton.setDisable(true); submitButton.setVisible(true);
                populateReviewSection();
            }
        }
        currentStep = step;
        updateStepTracker(step);
    }

    private void updateStepTracker(int step) {
        String completedStyle = "-fx-padding: 8 18; -fx-background-radius: 20; -fx-background-color: #2ecc71; -fx-text-fill: white;";
        String currentStyle   = "-fx-padding: 8 18; -fx-background-radius: 20; -fx-background-color: #3498db; -fx-text-fill: white;";
        String upcomingStyle  = "-fx-padding: 8 18; -fx-background-radius: 20; -fx-background-color: #ededed; -fx-text-fill: #7f8c8d;";

        Label[] steps = {stepLabel1, stepLabel2, stepLabel3, stepLabel4};

        for (int i = 0; i < steps.length; i++) {
            if (i < step - 1) steps[i].setStyle(completedStyle);
            else if (i == step - 1) steps[i].setStyle(currentStyle);
            else steps[i].setStyle(upcomingStyle);
        }
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

    @FXML
    private void handleNext(ActionEvent event) {
        if (validateCurrentStep()) showStep(currentStep + 1);
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
        return switch (currentStep) {
            case 1 -> validatePersonalInfo();
            case 2 -> validateContactInfo();
            case 3 -> validateAccountInfo();
            default -> true;
        };
    }

    private boolean validatePersonalInfo() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        LocalDate dob = dateOfBirthPicker.getValue();
        String gender = genderComboBox.getValue();

        if (firstName.isEmpty()) { showAlert("Missing Information", "First name is required.", Alert.AlertType.WARNING); return false; }
        if (lastName.isEmpty()) { showAlert("Missing Information", "Last name is required.", Alert.AlertType.WARNING); return false; }
        if (dob == null) { showAlert("Missing Information", "Date of birth is required.", Alert.AlertType.WARNING); return false; }
        if (gender == null) { showAlert("Missing Information", "Gender is required.", Alert.AlertType.WARNING); return false; }
        if (idNumber.isEmpty()) { showAlert("Missing Information", "ID number is required.", Alert.AlertType.WARNING); return false; }

        // ID format check
        if (!idNumber.matches("\\d{9}")) {
            showAlert("Invalid ID", "National ID must be exactly 9 digits.", Alert.AlertType.WARNING);
            return false;
        }

        // Gender middle digit match
        char middle = idNumber.charAt(4);
        if ((gender.equals("Male") && middle != '1') || (gender.equals("Female") && middle != '2')) {
            showAlert("Invalid ID", "Middle digit of ID does not match selected gender.", Alert.AlertType.WARNING);
            return false;
        }

        // Uniqueness check
        if (!accountService.isUniqueCustomer(idNumber, emailField.getText().trim(), phoneField.getText().trim())) {
            showAlert("Duplicate Entry", "A customer with this ID, email, or phone number already exists.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private boolean validateContactInfo() {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address1 = addressLine1Field.getText().trim();

        if (email.isEmpty()) { showAlert("Missing Information", "Email address is required.", Alert.AlertType.WARNING); return false; }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.WARNING); return false;
        }
        if (phone.isEmpty()) { showAlert("Missing Information", "Phone number is required.", Alert.AlertType.WARNING); return false; }
        if (!phone.matches("\\d+")) { showAlert("Invalid Phone", "Phone number must contain only digits.", Alert.AlertType.WARNING); return false; }
        if (address1.isEmpty()) { showAlert("Missing Information", "Address is required.", Alert.AlertType.WARNING); return false; }

        return true;
    }

    private boolean validateAccountInfo() {
        if (accountTypeComboBox.getValue() == null) { showAlert("Missing Information", "Account type is required.", Alert.AlertType.WARNING); return false; }
        if (initialDepositField.getText().trim().isEmpty()) { showAlert("Missing Information", "Initial deposit is required.", Alert.AlertType.WARNING); return false; }

        try {
            double deposit = Double.parseDouble(initialDepositField.getText());
            if (deposit <= 0) { showAlert("Invalid Amount", "Initial deposit must be greater than 0.", Alert.AlertType.WARNING); return false; }
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number for initial deposit.", Alert.AlertType.WARNING); return false;
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
                // Generate username & password
                String username = generateUsername(firstNameField.getText(), lastNameField.getText());
                String password = generatePassword(8);

                // Show account info + credentials
                showAlert("Success",
                        "Account created successfully!\n\n" +
                                "Account Number: " + accountNumber + "\n" +
                                "Account Type: " + accountTypeComboBox.getValue() + "\n" +
                                "Customer: " + firstNameField.getText() + " " + lastNameField.getText() + "\n\n" +
                                "Login Credentials:\nUsername: " + username + "\nPassword: " + password,
                        Alert.AlertType.INFORMATION);

                clearForm();
                showStep(1);
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
        if (currentUser instanceof Teller) return ((Teller) currentUser).getTellerId();
        return "UNKNOWN";
    }

    private String generateUsername(String firstName, String lastName) {
        return (firstName.charAt(0) + lastName).toLowerCase() + (int)(Math.random()*1000);
    }

    private String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
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

    @FXML private void handleLogout() {
        try { SceneNavigator.toLogin(); }
        catch (Exception e) { showAlert("Logout Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    private void clearForm() {
        firstNameField.clear(); lastNameField.clear(); dateOfBirthPicker.setValue(null);
        genderComboBox.setValue(null); idNumberField.clear(); emailField.clear(); phoneField.clear();
        addressLine1Field.clear(); addressLine2Field.clear(); cityField.clear(); stateField.clear(); postalCodeField.clear();
        initialDepositField.clear();

        if (preselectedAccountType != null) accountTypeComboBox.setValue(preselectedAccountType);
        else accountTypeComboBox.setValue(null);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
