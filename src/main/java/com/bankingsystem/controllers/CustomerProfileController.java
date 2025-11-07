package com.bankingsystem.controllers;

import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.db.AccountDAO;
import com.bankingsystem.db.CustomerDAO;
import com.bankingsystem.model.AccountWithCustomer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerProfileController implements Initializable {

    @FXML private Text usernameHeaderText;
    @FXML private TextField searchField;
    @FXML private Label totalAccountsLabel;
    @FXML private Text selectedCustomerText;
    @FXML private Label accountCountLabel;
    @FXML private Label selectedAccountLabel;

    @FXML private TableView<CustomerAccount> accountsTable;
    @FXML private TableColumn<CustomerAccount, String> accountNumberColumn;
    @FXML private TableColumn<CustomerAccount, String> customerNameColumn;
    @FXML private TableColumn<CustomerAccount, String> accountTypeColumn;
    @FXML private TableColumn<CustomerAccount, Double> balanceColumn;
    @FXML private TableColumn<CustomerAccount, String> statusColumn;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField idNumberField;
    @FXML private TextField otherInfoField;

    @FXML private VBox customerDetailsOverlay;
    @FXML private Button updateButton;

    private ObservableList<CustomerAccount> accountData = FXCollections.observableArrayList();
    private ObservableList<CustomerAccount> filteredData = FXCollections.observableArrayList();
    private CustomerAccount selectedCustomer;

    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        configureTable();
        setupTableSelectionListener();

        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        customerDetailsOverlay.setVisible(false);
        customerDetailsOverlay.setManaged(false);

        loadCustomerDataFromDatabase();

        filteredData.setAll(accountData);
        accountsTable.setItems(filteredData);
        updateTotalAccountsLabel(filteredData.size());
    }

    private void setupTableColumns() {
        accountNumberColumn.setCellValueFactory(cellData -> cellData.getValue().accountNumberProperty());
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        accountTypeColumn.setCellValueFactory(cellData -> cellData.getValue().accountTypeProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void configureTable() {
        accountsTable.setItems(filteredData);
    }

    private void setupTableSelectionListener() {
        accountsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedCustomer = newVal;
                        showCustomerDetailsOverlay(newVal);
                    } else {
                        hideCustomerDetailsOverlay();
                    }
                });
    }

    private void showCustomerDetailsOverlay(CustomerAccount customer) {
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        idNumberField.setText(customer.getIdNumber());
        otherInfoField.setText(customer.getContactInfo());

        selectedCustomerText.setText(customer.getCustomerName());
        accountCountLabel.setText(String.valueOf(customer.getAccountCount()));
        selectedAccountLabel.setText(customer.getAccountNumber());

        customerDetailsOverlay.setVisible(true);
        customerDetailsOverlay.setManaged(true);

        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        otherInfoField.setEditable(false);

        firstNameField.setStyle("-fx-background-color: #f8f9fa;");
        lastNameField.setStyle("-fx-background-color: #f8f9fa;");
        otherInfoField.setStyle("-fx-background-color: #f8f9fa;");

        updateButton.setText("Update Profile");
    }

    private void hideCustomerDetailsOverlay() {
        customerDetailsOverlay.setVisible(false);
        customerDetailsOverlay.setManaged(false);
        selectedCustomer = null;
    }

    @FXML private void handleCloseOverlay() {
        hideCustomerDetailsOverlay();
        accountsTable.getSelectionModel().clearSelection();
    }

    @FXML private void handleSearch() {
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
            if (filteredData.size() == 1) accountsTable.getSelectionModel().select(0);
            else hideCustomerDetailsOverlay();
        } else {
            handleViewAllCustomers();
        }
    }

    @FXML private void handleViewAllCustomers() {
        searchField.clear();
        filteredData.setAll(accountData);
        accountsTable.setItems(filteredData);
        updateTotalAccountsLabel(filteredData.size());
        hideCustomerDetailsOverlay();
    }

    @FXML private void handleUpdate() {
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer to update.", Alert.AlertType.ERROR);
            return;
        }

        if (!firstNameField.isEditable()) {
            // Enter edit mode
            firstNameField.setEditable(true);
            lastNameField.setEditable(true);
            otherInfoField.setEditable(true);
            firstNameField.setStyle("-fx-background-color: white;");
            lastNameField.setStyle("-fx-background-color: white;");
            otherInfoField.setStyle("-fx-background-color: white;");
            updateButton.setText("Save Changes");
        } else {
            saveCustomerUpdates();
        }
    }

    private void saveCustomerUpdates() {
        if (selectedCustomer == null) return;

        String updatedFirstName = firstNameField.getText().trim();
        String updatedLastName = lastNameField.getText().trim();
        String updatedContactInfo = otherInfoField.getText().trim();

        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
            showAlert("Validation Error", "First and Last Name cannot be empty.", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean success = customerDAO.updateCustomer(
                    selectedCustomer.getIdNumber(),
                    updatedFirstName,
                    updatedLastName,
                    updatedContactInfo
            );

            if (success) {
                selectedCustomer.setFirstName(updatedFirstName);
                selectedCustomer.setLastName(updatedLastName);
                selectedCustomer.setContactInfo(updatedContactInfo);

                accountsTable.refresh();

                firstNameField.setEditable(false);
                lastNameField.setEditable(false);
                otherInfoField.setEditable(false);

                firstNameField.setStyle("-fx-background-color: #f8f9fa;");
                lastNameField.setStyle("-fx-background-color: #f8f9fa;");
                otherInfoField.setStyle("-fx-background-color: #f8f9fa;");

                updateButton.setText("Update Profile");

                showAlert("Success", "Customer updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update customer in the database.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update customer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleBackToDashboard() {
        try { SceneNavigator.toTellerDashboard(); }
        catch (Exception e) { showAlert("Navigation Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleTermsAndConditions() { showTermsAndConditions(); }

    @FXML private void handleLogout() {
        try { SceneNavigator.toLogin(); }
        catch (Exception e) { showAlert("Logout Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    private void loadCustomerDataFromDatabase() {
        accountData.clear();
        try {
            List<AccountWithCustomer> accountsWithCustomer = accountDAO.findAllAccountsWithCustomerInfo();

            // First, count accounts per customer
            Map<String, Integer> customerAccountCount = new HashMap<>();
            for (AccountWithCustomer acc : accountsWithCustomer) {
                customerAccountCount.put(acc.getNationalId(),
                        customerAccountCount.getOrDefault(acc.getNationalId(), 0) + 1);
            }

            for (AccountWithCustomer acc : accountsWithCustomer) {
                accountData.add(convertToCustomerAccount(acc, customerAccountCount));
            }

            updateTotalAccountsLabel(accountData.size());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private CustomerAccount convertToCustomerAccount(AccountWithCustomer acc, Map<String, Integer> accountCountMap) {
        String fullName = acc.getCustomerName() != null ? acc.getCustomerName() : "";
        String firstName = "";
        String lastName = "";
        if (!fullName.isEmpty()) {
            String[] parts = fullName.split(" ", 2);
            firstName = parts[0];
            lastName = parts.length > 1 ? parts[1] : "";
        }

        String contactInfo = "";
        if (acc.getPhoneNumber() != null) contactInfo = acc.getPhoneNumber();
        if (acc.getEmail() != null && !acc.getEmail().isEmpty()) {
            if (!contactInfo.isEmpty()) contactInfo += " | ";
            contactInfo += acc.getEmail();
        }

        int accountCount = accountCountMap.getOrDefault(acc.getNationalId(), 1);

        return new CustomerAccount(
                acc.getAccountNumber(),
                firstName,
                lastName,
                acc.getNationalId(),
                acc.getAccountType(),
                acc.getBalance().doubleValue(),
                acc.getStatus(),
                accountCount,
                contactInfo
        );
    }

    private void showTermsAndConditions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Terms and Conditions");
        alert.setHeaderText("Sediba Financial - Terms and Conditions");
        alert.setContentText("Terms and Conditions content...");
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTotalAccountsLabel(int count) {
        totalAccountsLabel.setText(String.valueOf(count));
    }

    public void setTellerInfo(String username) {
        if (usernameHeaderText != null) usernameHeaderText.setText(username);
    }

    // --------- Data model ---------
    public static class CustomerAccount {
        private final StringProperty accountNumber;
        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty idNumber;
        private final StringProperty accountType;
        private final DoubleProperty balance;
        private final StringProperty status;
        private final IntegerProperty accountCount;
        private final StringProperty contactInfo;

        public CustomerAccount(String accountNumber, String firstName, String lastName, String idNumber,
                               String accountType, double balance, String status, int accountCount, String contactInfo) {
            this.accountNumber = new SimpleStringProperty(accountNumber);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.idNumber = new SimpleStringProperty(idNumber);
            this.accountType = new SimpleStringProperty(accountType);
            this.balance = new SimpleDoubleProperty(balance);
            this.status = new SimpleStringProperty(status);
            this.accountCount = new SimpleIntegerProperty(accountCount);
            this.contactInfo = new SimpleStringProperty(contactInfo);
        }

        public String getAccountNumber() { return accountNumber.get(); }
        public StringProperty accountNumberProperty() { return accountNumber; }

        public String getCustomerName() { return getFirstName() + " " + getLastName(); }
        public StringProperty customerNameProperty() { return new SimpleStringProperty(getCustomerName()); }

        public String getAccountType() { return accountType.get(); }
        public StringProperty accountTypeProperty() { return accountType; }

        public double getBalance() { return balance.get(); }
        public DoubleProperty balanceProperty() { return balance; }

        public String getStatus() { return status.get(); }
        public StringProperty statusProperty() { return status; }

        public String getFirstName() { return firstName.get(); }
        public void setFirstName(String value) { firstName.set(value); }

        public String getLastName() { return lastName.get(); }
        public void setLastName(String value) { lastName.set(value); }

        public String getIdNumber() { return idNumber.get(); }
        public void setIdNumber(String value) { idNumber.set(value); }

        public int getAccountCount() { return accountCount.get(); }
        public void setAccountCount(int value) { accountCount.set(value); }

        public String getContactInfo() { return contactInfo.get(); }
        public void setContactInfo(String value) { contactInfo.set(value); }
    }
}
