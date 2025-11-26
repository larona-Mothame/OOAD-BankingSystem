package com.bankingsystem.controllers;

import com.bankingsystem.model.CompanyCustomer;
import com.bankingsystem.model.Teller;
import com.bankingsystem.service.AccountService;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountOpeningController {


    @FXML private Text progressText;

    @FXML private Label stepLabel1;
    @FXML private Label stepLabel2;
    @FXML private Label stepLabel3;
    @FXML private Label stepLabel4;

    @FXML private VBox step1Personal;
    @FXML private VBox step1Company;
    @FXML private VBox step2Contact;
    @FXML private VBox step3Account;
    @FXML private VBox step4Review;

    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;

    // Customer type
    @FXML private ComboBox<String> customerTypeComboBox;

    // Individual Info
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> idTypeComboBox;
    @FXML private TextField idNumberField;
    @FXML private TextField nationalityField;

    // Company Info
    @FXML private TextField companyNameField;
    @FXML private TextField companyRegistrationNumberField;
    @FXML private TextField primaryContactField;

    // Contact Info (shared)
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

    // Review Fields
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

        customerTypeComboBox.setOnAction(e -> {
            boolean isIndividual = customerTypeComboBox.getValue().equals("Individual");
            toggleStep(step1Personal, isIndividual);
            toggleStep(step1Company, !isIndividual);
        });
    }

    private void setupComboBoxes() {
        customerTypeComboBox.setItems(FXCollections.observableArrayList("Individual", "Company"));
        customerTypeComboBox.setValue("Individual");

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

    private void toggleStep(VBox stepBox, boolean show) {
        stepBox.setVisible(show);
        stepBox.setManaged(show);
    }

    private void showStep(int step) {
        toggleStep(step1Personal, false);
        toggleStep(step1Company, false);
        toggleStep(step2Contact, false);
        toggleStep(step3Account, false);
        toggleStep(step4Review, false);

        switch (step) {
            case 1 -> {
                toggleStep(customerTypeComboBox.getValue().equals("Individual") ? step1Personal : step1Company, true);
                progressText.setText("Step 1: Personal / Company Information");
                backButton.setDisable(true);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
            }
            case 2 -> {
                toggleStep(step2Contact, true);
                progressText.setText("Step 2: Contact Information");
                backButton.setDisable(false);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
            }
            case 3 -> {
                toggleStep(step3Account, true);
                progressText.setText("Step 3: Account Information");
                backButton.setDisable(false);
                nextButton.setDisable(false);
                submitButton.setVisible(false);
            }
            case 4 -> {
                toggleStep(step4Review, true);
                progressText.setText("Step 4: Review & Submit");
                backButton.setDisable(false);
                nextButton.setDisable(true);
                submitButton.setVisible(true);
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
        Map<TextField, TextField> reviewMap = Map.of(
                reviewEmail, emailField,
                reviewPhone, phoneField,
                reviewInitialDeposit, initialDepositField
        );
        reviewMap.forEach((review, original) -> review.setText(original.getText()));

        if (customerTypeComboBox.getValue().equals("Individual")) {
            reviewFirstName.setText(firstNameField.getText());
            reviewLastName.setText(lastNameField.getText());
            reviewIdNumber.setText(idNumberField.getText());
        } else {
            reviewFirstName.setText(companyNameField.getText());
            reviewLastName.setText("");
            reviewIdNumber.setText(companyRegistrationNumberField.getText());
        }
        reviewAccountType.setText(accountTypeComboBox.getValue());
    }

    @FXML private void handleNext(ActionEvent event) {
        if (validateCurrentStep()) showStep(currentStep + 1);
    }

    @FXML private void handleBack(ActionEvent event) {
        showStep(currentStep - 1);
    }

    @FXML private void handleSubmit(ActionEvent event) {
        if (!validateForm()) return;

        if (customerTypeComboBox.getValue().equals("Individual")) createIndividualAccount();
        else createCompanyAccount();
    }

    private boolean validateCurrentStep() {
        if (customerTypeComboBox.getValue().equals("Company") && currentStep == 1) return validateCompanyInfo();
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
        if (!idNumber.matches("\\d{9}")) { showAlert("Invalid ID", "National ID must be exactly 9 digits.", Alert.AlertType.WARNING); return false; }

        if (!gender.equals("Other")) {
            char middle = idNumber.charAt(4);
            if ((gender.equals("Male") && middle != '1') || (gender.equals("Female") && middle != '2')) {
                showAlert("Invalid ID", "Middle digit of ID does not match selected gender.", Alert.AlertType.WARNING);
                return false;
            }
        }

        if (!accountService.isUniqueCustomer(idNumber, emailField.getText().trim(), phoneField.getText().trim())) {
            showAlert("Duplicate Entry", "A customer with this ID, email, or phone number already exists.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean validateCompanyInfo() {
        if (companyNameField.getText().trim().isEmpty()) { showAlert("Missing Information", "Company name is required.", Alert.AlertType.WARNING); return false; }
        if (companyRegistrationNumberField.getText().trim().isEmpty()) { showAlert("Missing Information", "Registration number is required.", Alert.AlertType.WARNING); return false; }
        if (primaryContactField.getText().trim().isEmpty()) { showAlert("Missing Information", "Primary contact person is required.", Alert.AlertType.WARNING); return false; }
        if (!accountService.isUniqueCustomer(companyRegistrationNumberField.getText(), emailField.getText(), phoneField.getText())) {
            showAlert("Duplicate Entry", "A company with this registration number, email, or phone already exists.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean validateContactInfo() {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address1 = addressLine1Field.getText().trim();

        if (email.isEmpty()) { showAlert("Missing Information", "Email address is required.", Alert.AlertType.WARNING); return false; }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) { showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.WARNING); return false; }
        if (phone.isEmpty()) { showAlert("Missing Information", "Phone number is required.", Alert.AlertType.WARNING); return false; }
        if (!phone.matches("\\d+")) { showAlert("Invalid Phone", "Phone number must contain only digits.", Alert.AlertType.WARNING); return false; }
        if (address1.isEmpty()) { showAlert("Missing Information", "Address is required.", Alert.AlertType.WARNING); return false; }

        return true;
    }

    private boolean validateAccountInfo() {
        if (accountTypeComboBox.getValue() == null) { showAlert("Missing Information", "Account type is required.", Alert.AlertType.WARNING); return false; }
        if (initialDepositField.getText().trim().isEmpty()) { showAlert("Missing Information", "Initial deposit is required.", Alert.AlertType.WARNING); return false; }
        try {
            double deposit = Double.parseDouble(initialDepositField.getText().replace(",", "").trim());
            if (deposit <= 0) { showAlert("Invalid Amount", "Initial deposit must be greater than 0.", Alert.AlertType.WARNING); return false; }
        } catch (NumberFormatException e) { showAlert("Invalid Amount", "Please enter a valid number for initial deposit.", Alert.AlertType.WARNING); return false; }
        return true;
    }

    private boolean validateForm() {
        return validateCurrentStep() && validateContactInfo() && validateAccountInfo();
    }

    private void createIndividualAccount() {
        accountService.createIndividualAccount(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                idNumberField.getText().trim(),
                dateOfBirthPicker.getValue(),
                genderComboBox.getValue(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                Stream.of(addressLine1Field, addressLine2Field, cityField, stateField, postalCodeField)
                        .map(TextField::getText)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")),
                accountTypeComboBox.getValue(),
                new BigDecimal(initialDepositField.getText().replace(",", "").trim()),
                getCurrentTellerId()
        );
        showAlert("Success", "Individual account created successfully.", Alert.AlertType.INFORMATION);
        clearForm();
        showStep(1);
    }

    private void createCompanyAccount() {
        CompanyCustomer company = new CompanyCustomer(
                companyNameField.getText(),
                companyRegistrationNumberField.getText(),
                primaryContactField.getText(),
                phoneField.getText(),
                emailField.getText(),
                Stream.of(addressLine1Field, addressLine2Field, cityField, stateField, postalCodeField)
                        .map(TextField::getText)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", "))
        );

        String accountNumber = accountService.createCompanyAccount(company, accountTypeComboBox.getValue(),
                new BigDecimal(initialDepositField.getText().replace(",", "").trim()), getCurrentTellerId());

        if (accountNumber != null) {
            showAlert("Success", "Company account created successfully!\nAccount Number: " + accountNumber, Alert.AlertType.INFORMATION);
            clearForm();
            showStep(1);
        } else showAlert("Error", "Failed to create company account.", Alert.AlertType.ERROR);
    }

    private String getCurrentTellerId() {
        Object currentUser = SessionManager.getCurrentUser();
        return (currentUser instanceof Teller) ? ((Teller) currentUser).getTellerId() : "UNKNOWN";
    }

    @FXML private void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "All entered data will be lost.", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Cancel Application");
        alert.setHeaderText("Are you sure you want to cancel this application?");
        alert.showAndWait().ifPresent(response -> { if (response == ButtonType.OK) { clearForm(); showStep(1); } });
    }

    @FXML private void handleBackToDashboard(ActionEvent event) {
        try { SceneNavigator.toTellerDashboard(); } catch (Exception e) { showAlert("Navigation Error", "Failed to navigate to dashboard: " + e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleViewTerms(ActionEvent event) {
        showAlert("Terms and Conditions",
                "Sediba Financial Terms:\n- No overdraft allowed.\n- Minimum balance applies per account type.",
                Alert.AlertType.INFORMATION);
    }

    @FXML private void handleViewPrivacy(ActionEvent event) {
        showAlert("Privacy Policy",
                "Your data will be stored securely and used only for banking operations.",
                Alert.AlertType.INFORMATION);
    }

    @FXML private void handleLogout() {
        try { SceneNavigator.toLogin(); } catch (Exception e) { showAlert("Logout Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    private void clearForm() {
        Stream.of(firstNameField, lastNameField, idNumberField, companyNameField, companyRegistrationNumberField, primaryContactField,
                        emailField, phoneField, addressLine1Field, addressLine2Field, cityField, stateField, postalCodeField, initialDepositField)
                .forEach(tf -> tf.clear());
        dateOfBirthPicker.setValue(null);
        genderComboBox.setValue(null);

        if (preselectedAccountType != null) accountTypeComboBox.setValue(preselectedAccountType);
        else accountTypeComboBox.setValue(null);

        customerTypeComboBox.setValue("Individual");
        toggleStep(step1Personal, true);
        toggleStep(step1Company, false);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
