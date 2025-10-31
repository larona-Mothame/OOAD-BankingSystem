package com.bankingsystem.controllers;

import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.db.AccountDAO;
import com.bankingsystem.db.CustomerDAO;
import com.bankingsystem.model.AccountWithCustomer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerProfileController implements Initializable {

    @FXML private Text usernameHeaderText;
    @FXML private TextField searchField;
    @FXML private Label totalAccountsLabel;
    @FXML private Text selectedCustomerText;
    @FXML private Label accountCountLabel;
    @FXML private Label selectedAccountLabel;

    // TableView and columns
    @FXML private TableView<CustomerAccount> accountsTable;
    @FXML private TableColumn<CustomerAccount, String> accountNumberColumn;
    @FXML private TableColumn<CustomerAccount, String> customerNameColumn;
    @FXML private TableColumn<CustomerAccount, String> accountTypeColumn;
    @FXML private TableColumn<CustomerAccount, Double> balanceColumn;
    @FXML private TableColumn<CustomerAccount, String> statusColumn;

    // Customer details fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField idNumberField;
    @FXML private TextField otherInfoField;

    // Overlay container
    @FXML private VBox customerDetailsOverlay;

    private ObservableList<CustomerAccount> accountData = FXCollections.observableArrayList();
    private ObservableList<CustomerAccount> filteredData = FXCollections.observableArrayList();
    private CustomerAccount selectedCustomer;

    // DAOs for database operations
    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        configureTable();
        setupTableSelectionListener();

        // Set constrained resize policy programmatically
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ensure overlay is hidden by default
        customerDetailsOverlay.setVisible(false);
        customerDetailsOverlay.setManaged(false);

        // Load real data from database
        loadCustomerDataFromDatabase();

        // Initialize with all data
        filteredData.setAll(accountData);
        accountsTable.setItems(filteredData);
        updateTotalAccountsLabel(filteredData.size());
    }

    private void setupTableColumns() {
        accountNumberColumn.setCellValueFactory(cellData ->
                cellData.getValue().accountNumberProperty());

        customerNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().customerNameProperty());

        accountTypeColumn.setCellValueFactory(cellData ->
                cellData.getValue().accountTypeProperty());

        balanceColumn.setCellValueFactory(cellData ->
                cellData.getValue().balanceProperty().asObject());

        statusColumn.setCellValueFactory(cellData ->
                cellData.getValue().statusProperty());
    }

    private void configureTable() {
        accountsTable.setItems(filteredData);
    }

    private void setupTableSelectionListener() {
        accountsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedCustomer = newValue;
                        showCustomerDetailsOverlay(newValue);
                    } else {
                        hideCustomerDetailsOverlay();
                    }
                });
    }

    private void showCustomerDetailsOverlay(CustomerAccount customer) {
        // Populate customer details
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        idNumberField.setText(customer.getIdNumber());
        otherInfoField.setText(customer.getContactInfo());

        // Update labels
        selectedCustomerText.setText(customer.getCustomerName());
        accountCountLabel.setText(String.valueOf(customer.getAccountCount()));
        selectedAccountLabel.setText(customer.getAccountNumber());

        // Show the overlay
        customerDetailsOverlay.setVisible(true);
        customerDetailsOverlay.setManaged(true);

        // Position the overlay (centered)
        customerDetailsOverlay.setTranslateX(0);
        customerDetailsOverlay.setTranslateY(0);
    }

    private void hideCustomerDetailsOverlay() {
        customerDetailsOverlay.setVisible(false);
        customerDetailsOverlay.setManaged(false);
        selectedCustomer = null;
    }

    @FXML
    private void handleCloseOverlay() {
        hideCustomerDetailsOverlay();
        accountsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            filteredData.clear();
            for (CustomerAccount account : accountData) {
                if (account.getCustomerName().toLowerCase().contains(searchTerm) ||
                        account.getAccountNumber().toLowerCase().contains(searchTerm) ||
                        account.getIdNumber().toLowerCase().contains(searchTerm)) {
                    filteredData.add(account);
                }
            }
            updateTotalAccountsLabel(filteredData.size());

            // If only one result, automatically select it
            if (filteredData.size() == 1) {
                accountsTable.getSelectionModel().select(0);
            } else {
                hideCustomerDetailsOverlay();
            }
        } else {
            handleViewAllCustomers();
        }
    }

    @FXML
    private void handleViewAllCustomers() {
        searchField.clear();
        filteredData.setAll(accountData);
        accountsTable.setItems(filteredData);
        updateTotalAccountsLabel(filteredData.size());

        hideCustomerDetailsOverlay();
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer != null) {
            updateExistingCustomer();
        } else {
            showAlert("Error", "Please select a customer to update.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            SceneNavigator.toTellerDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleTermsAndConditions() {
        showTermsAndConditions();
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.toLogin();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to logout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Database methods (same as before)
    private void loadCustomerDataFromDatabase() {
        accountData.clear();

        try {
            List<AccountWithCustomer> accountsWithCustomer = accountDAO.findAllAccountsWithCustomerInfo();

            for (AccountWithCustomer accountWithCustomer : accountsWithCustomer) {
                CustomerAccount customerAccount = convertToCustomerAccount(accountWithCustomer);
                accountData.add(customerAccount);
            }

            updateTotalAccountsLabel(accountData.size());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load customer data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private CustomerAccount convertToCustomerAccount(AccountWithCustomer accountWithCustomer) {
        String fullName = accountWithCustomer.getCustomerName();
        String firstName = "";
        String lastName = "";

        if (fullName != null && !fullName.isEmpty()) {
            String[] nameParts = fullName.split(" ", 2);
            firstName = nameParts.length > 0 ? nameParts[0] : "";
            lastName = nameParts.length > 1 ? nameParts[1] : "";
        }

        String contactInfo = "";
        if (accountWithCustomer.getPhoneNumber() != null) {
            contactInfo = accountWithCustomer.getPhoneNumber();
        }
        if (accountWithCustomer.getEmail() != null && !accountWithCustomer.getEmail().isEmpty()) {
            if (!contactInfo.isEmpty()) {
                contactInfo += " | ";
            }
            contactInfo += accountWithCustomer.getEmail();
        }

        int accountCount = getAccountCountForCustomer(accountWithCustomer.getNationalId());

        return new CustomerAccount(
                accountWithCustomer.getAccountNumber(),
                firstName,
                lastName,
                accountWithCustomer.getNationalId(),
                accountWithCustomer.getAccountType(),
                accountWithCustomer.getBalance().doubleValue(),
                accountWithCustomer.getStatus(),
                String.valueOf(accountCount),
                contactInfo
        );
    }

    private int getAccountCountForCustomer(String nationalId) {
        int count = 0;
        for (CustomerAccount account : accountData) {
            if (account.getIdNumber().equals(nationalId)) {
                count++;
            }
        }
        return count > 0 ? count : 1;
    }

    private void updateExistingCustomer() {
        try {
            showAlert("Update Profile", "Customer update functionality will be implemented here.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update customer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showTermsAndConditions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Terms and Conditions");
        alert.setHeaderText("Sediba Financial - Terms and Conditions");
        alert.setContentText("Terms and Conditions content...");
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTotalAccountsLabel(int count) {
        totalAccountsLabel.setText(String.valueOf(count));
    }

    public void setTellerInfo(String username) {
        if (usernameHeaderText != null) {
            usernameHeaderText.setText(username);
        }
    }

    // Data model class
    public static class CustomerAccount {
        private final String accountNumber;
        private String firstName;
        private String lastName;
        private String idNumber;
        private String accountType;
        private double balance;
        private String status;
        private String accountCount;
        private String contactInfo;

        public CustomerAccount(String accountNumber, String firstName, String lastName, String idNumber,
                               String accountType, double balance, String status,
                               String accountCount, String contactInfo) {
            this.accountNumber = accountNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.idNumber = idNumber;
            this.accountType = accountType;
            this.balance = balance;
            this.status = status;
            this.accountCount = accountCount;
            this.contactInfo = contactInfo;
        }

        // Property methods
        public String getAccountNumber() { return accountNumber; }
        public javafx.beans.property.StringProperty accountNumberProperty() {
            return new javafx.beans.property.SimpleStringProperty(accountNumber);
        }

        public String getCustomerName() { return firstName + " " + lastName; }
        public javafx.beans.property.StringProperty customerNameProperty() {
            return new javafx.beans.property.SimpleStringProperty(getCustomerName());
        }

        public String getAccountType() { return accountType; }
        public javafx.beans.property.StringProperty accountTypeProperty() {
            return new javafx.beans.property.SimpleStringProperty(accountType);
        }

        public double getBalance() { return balance; }
        public javafx.beans.property.DoubleProperty balanceProperty() {
            return new javafx.beans.property.SimpleDoubleProperty(balance);
        }

        public String getStatus() { return status; }
        public javafx.beans.property.StringProperty statusProperty() {
            return new javafx.beans.property.SimpleStringProperty(status);
        }

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getIdNumber() { return idNumber; }
        public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

        public String getAccountCount() { return accountCount; }
        public void setAccountCount(String accountCount) { this.accountCount = accountCount; }

        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    }
}