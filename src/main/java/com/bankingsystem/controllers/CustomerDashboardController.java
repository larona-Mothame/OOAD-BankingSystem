package com.bankingsystem.controllers;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.ChequeAccount;
import com.bankingsystem.model.SavingsAccount;
import com.bankingsystem.model.InvestmentAccount;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import com.bankingsystem.db.AccountDAO;
import com.bankingsystem.db.TransactionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML private Text transactionTitle;
    @FXML private Text selectedAccountText;
    @FXML private Text selectedBalanceText;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> transferAccountComboBox;
    @FXML private VBox transferSection;

    // --- Transaction Buttons ---
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;

    // --- Transactions Table ---
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> accountColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private TableColumn<Transaction, String> tellerColumn;

    @FXML private ComboBox<String> transactionFilterComboBox;

    // --- DAOs and Current User ---
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private Customer currentCustomer;
    private Account currentlySelectedAccount;

    // Date formatter for transaction dates
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        System.out.println("DEBUG: Initializing CustomerDashboardController");

        // Initialize DAOs
        accountDAO = new AccountDAO();
        transactionDAO = new TransactionDAO();

        // Get current customer from session
        currentCustomer = (Customer) SessionManager.getCurrentUser();
        System.out.println("DEBUG: Current customer: " + (currentCustomer != null ? currentCustomer.getCustomerId() : "null"));

        if (currentCustomer != null) {
            loadCustomerData();
            loadAccounts();
            loadRecentTransactions();
            setupTransactionTable();
        } else {
            showAlert(AlertType.ERROR, "Session Error", "No customer session found. Please login again.");
        }

        // Setup transaction filter
        transactionFilterComboBox.setItems(FXCollections.observableArrayList("All Transactions", "Last 30 Days", "Last 7 Days"));
        transactionFilterComboBox.getSelectionModel().selectFirst();

        // Make sure welcome panel is visible by default
        welcomePanel.setVisible(true);
        welcomePanel.setManaged(true);
        transactionPanel.setVisible(false);
        transactionPanel.setManaged(false);
    }

    private void loadCustomerData() {
        customerNameText.setText(currentCustomer.getFullName());
        double totalBalance = calculateTotalBalance();
        totalBalanceLabel.setText(String.format("Total: BWP %,.2f", totalBalance));
    }

    private void loadAccounts() {
        try {
            System.out.println("DEBUG: Loading accounts for customer: " + currentCustomer.getCustomerId());

            List<Account> accounts = accountDAO.findAccountsByCustomerId(currentCustomer.getCustomerId());

            System.out.println("DEBUG: Found " + accounts.size() + " accounts");
            for (Account a : accounts) {
                System.out.println("DEBUG: Account - " + a.getAccountNumber() + " Balance: " + a.getBalance());
            }

            totalAccountsText.setText(String.valueOf(accounts.size()));
            accountsContainer.getChildren().clear();

            for (Account account : accounts) {
                VBox accountCard = createAccountCard(account);
                accountsContainer.getChildren().add(accountCard);
            }

        } catch (Exception e) {
            System.err.println("ERROR loading accounts: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Data Error", "Failed to load accounts: " + e.getMessage());
        }
    }

    private void loadRecentTransactions() {
        try {
            System.out.println("DEBUG: Loading transactions for customer: " + currentCustomer.getCustomerId());

            List<Transaction> transactions = transactionDAO.findRecentTransactionsByCustomerId(currentCustomer.getCustomerId(), 10);

            System.out.println("DEBUG: Found " + transactions.size() + " transactions");
            for (Transaction t : transactions) {
                System.out.println("DEBUG: Transaction - " + t.getType() + " " + t.getAmount() + " for account " + t.getAccountNumber());
            }

            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
            transactionsTable.setItems(transactionList);

            if (!transactions.isEmpty()) {
                Transaction lastTransaction = transactions.get(0);
                String amountText = String.format("BWP %,.2f", lastTransaction.getAmount().doubleValue());
                lastTransactionText.setText(String.format("%s - %s",
                        lastTransaction.getType().toString(),
                        amountText));
                calculateMonthlySpending();
            } else {
                lastTransactionText.setText("No transactions");
                monthlySpendingText.setText("BWP 0.00");
            }

        } catch (Exception e) {
            System.err.println("ERROR loading transactions: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Data Error", "Failed to load transactions: " + e.getMessage());
        }
    }

    private void setupTransactionTable() {
        try {
            System.out.println("DEBUG: Setting up transaction table columns");

            if (dateColumn != null) {
                dateColumn.setCellValueFactory(cellData -> {
                    String formattedDate = cellData.getValue().getTimestamp().format(dateFormatter);
                    return new javafx.beans.property.SimpleStringProperty(formattedDate);
                });
            }

            if (typeColumn != null) {
                typeColumn.setCellValueFactory(cellData -> {
                    String type = cellData.getValue().getType().toString();
                    return new javafx.beans.property.SimpleStringProperty(type);
                });
            }

            if (accountColumn != null) {
                accountColumn.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountNumber()));
            }

            if (amountColumn != null) {
                amountColumn.setCellValueFactory(cellData -> {
                    BigDecimal amount = cellData.getValue().getAmount();
                    String amountText = String.format("BWP %,.2f", amount.doubleValue());
                    return new javafx.beans.property.SimpleStringProperty(amountText);
                });

                amountColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
                    @Override
                    protected void updateItem(String amount, boolean empty) {
                        super.updateItem(amount, empty);
                        if (empty || amount == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(amount);
                            Transaction transaction = getTableView().getItems().get(getIndex());
                            if (transaction.getType() == Transaction.Type.DEPOSIT) {
                                setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            } else {
                                setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            }
                        }
                    }
                });
            }

            if (tellerColumn != null) {
                tellerColumn.setCellValueFactory(cellData -> {
                    String tellerId = cellData.getValue().getTellerId();
                    return new javafx.beans.property.SimpleStringProperty(tellerId != null ? tellerId : "Self-Service");
                });
            }

            System.out.println("DEBUG: Transaction table setup completed successfully");
        } catch (Exception e) {
            System.err.println("ERROR setting up transaction table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double calculateTotalBalance() {
        try {
            List<Account> accounts = accountDAO.findAccountsByCustomerId(currentCustomer.getCustomerId());
            return accounts.stream()
                    .mapToDouble(account -> account.getBalance().doubleValue())
                    .sum();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Calculation Error", "Failed to calculate total balance: " + e.getMessage());
            return 0.0;
        }
    }

    private void calculateMonthlySpending() {
        try {
            List<Transaction> monthlyTransactions = transactionDAO.findTransactionsByCustomerIdAndPeriod(currentCustomer.getCustomerId(), 30);
            BigDecimal monthlySpending = monthlyTransactions.stream()
                    .filter(t -> t.getType() == Transaction.Type.WITHDRAWAL)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlySpendingText.setText(String.format("BWP %,.2f", monthlySpending.doubleValue()));
        } catch (Exception e) {
            monthlySpendingText.setText("BWP 0.00");
        }
    }

    private VBox createAccountCard(Account account) {
        VBox card = new VBox();
        card.getStyleClass().add("account-card");
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        Label accountNumber = new Label("Account: " + account.getAccountNumber());
        Label accountType = new Label(getAccountTypeDisplay(account));
        Label balance = new Label(String.format("BWP %,.2f", account.getBalance().doubleValue()));

        accountNumber.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        accountType.setStyle("-fx-text-fill: #7f8c8d;");
        balance.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 16;");

        Label features = new Label(getAccountFeatures(account));
        features.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12;");

        card.getChildren().addAll(accountNumber, accountType, balance, features);
        card.setOnMouseClicked(event -> handleAccountSelection(account));

        return card;
    }

    private String getAccountTypeDisplay(Account account) {
        if (account instanceof ChequeAccount) return "Cheque Account";
        else if (account instanceof SavingsAccount) return "Savings Account";
        else if (account instanceof InvestmentAccount) return "Investment Account";
        else return "Account";
    }

    private String getAccountFeatures(Account account) {
        if (account instanceof ChequeAccount) return "✓ Daily transactions allowed";
        else if (account instanceof SavingsAccount) return "✓ No withdrawals | ✓ 0.05% monthly interest";
        else if (account instanceof InvestmentAccount) return "✓ Min. BWP 500 | ✓ 5% monthly interest";
        else return "";
    }

    private void handleAccountSelection(Account account) {
        currentlySelectedAccount = account;
        selectedAccountText.setText(account.getAccountNumber());
        selectedBalanceText.setText(String.format("BWP %,.2f", account.getBalance().doubleValue()));
        updateTransactionOptions(account);
        openTransactionPanel();
    }

    private void updateTransactionOptions(Account account) {
        transferSection.setVisible(false);

        if (account instanceof ChequeAccount) {
            transactionTitle.setText("Cheque Account");
            depositButton.setVisible(true);
            depositButton.setManaged(true);
            withdrawButton.setVisible(true);
            withdrawButton.setManaged(true);
        } else if (account instanceof SavingsAccount) {
            transactionTitle.setText("Savings Account");
            depositButton.setVisible(true);
            depositButton.setManaged(true);
            withdrawButton.setVisible(false);
            withdrawButton.setManaged(false);
            showAlert(AlertType.INFORMATION, "Savings Account",
                    "Withdrawals from savings accounts are not allowed. You can only deposit funds.");
        } else if (account instanceof InvestmentAccount) {
            transactionTitle.setText("Investment Account");
            depositButton.setVisible(true);
            depositButton.setManaged(true);
            withdrawButton.setVisible(true);
            withdrawButton.setManaged(true);
        }
    }

    @FXML
    private void handleCancelTransaction() {
        closeTransactionPanel();
    }

    @FXML
    private void handleDeposit() {
        try {
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (amountText.isEmpty()) {
                showAlert(AlertType.WARNING, "Invalid Input", "Please enter a valid deposit amount.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            Account selectedAccount = getSelectedAccount();

            if (selectedAccount == null) {
                showAlert(AlertType.ERROR, "Error", "No account selected.");
                return;
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(AlertType.ERROR, "Invalid Amount", "Deposit amount must be greater than zero.");
                return;
            }

            // Process deposit
            handleDepositTransaction(selectedAccount, amount, description);

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric amount.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Transaction Error", "Failed to process deposit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWithdraw() {
        try {
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (amountText.isEmpty()) {
                showAlert(AlertType.WARNING, "Invalid Input", "Please enter a valid withdrawal amount.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            Account selectedAccount = getSelectedAccount();

            if (selectedAccount == null) {
                showAlert(AlertType.ERROR, "Error", "No account selected.");
                return;
            }

            // Check if withdrawal is allowed for this account type
            if (selectedAccount instanceof SavingsAccount) {
                showAlert(AlertType.ERROR, "Operation Not Allowed",
                        "Withdrawals from savings accounts are not allowed.");
                return;
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(AlertType.ERROR, "Invalid Amount", "Withdrawal amount must be greater than zero.");
                return;
            }

            // Process withdrawal
            handleWithdrawalTransaction(selectedAccount, amount, description);

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric amount.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Transaction Error", "Failed to process withdrawal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDepositTransaction(Account account, BigDecimal amount, String description) {
        try {
            System.out.println("DEBUG: Processing deposit - Account: " + account.getAccountNumber() +
                    ", Amount: " + amount);

            account.deposit(amount);

            System.out.println("DEBUG: After deposit - New Balance: " + account.getBalance());

            // Update account balance in database
            boolean updateSuccess = accountDAO.updateAccountBalance(account.getAccountNumber(), account.getBalance());

            if (!updateSuccess) {
                throw new Exception("Failed to update account balance in database");
            }

            // Create and save transaction record
            Transaction transaction = new Transaction(account.getAccountNumber(), Transaction.Type.DEPOSIT, amount, null);
            boolean saveSuccess = transactionDAO.saveTransaction(transaction);

            if (!saveSuccess) {
                throw new Exception("Failed to save transaction record");
            }

            System.out.println("DEBUG: Deposit completed successfully");
            showAlert(AlertType.INFORMATION, "Deposit Complete",
                    String.format("Deposited BWP %,.2f to account %s", amount.doubleValue(), account.getAccountNumber()));

            closeTransactionPanel();
            refreshData();

        } catch (UnsupportedOperationException e) {
            showAlert(AlertType.ERROR, "Operation Not Allowed", e.getMessage());
        } catch (IllegalStateException e) {
            showAlert(AlertType.ERROR, "Transaction Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to save deposit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleWithdrawalTransaction(Account account, BigDecimal amount, String description) {
        try {
            System.out.println("DEBUG: Processing withdrawal - Account: " + account.getAccountNumber() +
                    ", Amount: " + amount);

            account.withdraw(amount);

            System.out.println("DEBUG: After withdrawal - New Balance: " + account.getBalance());

            // Update account balance in database
            boolean updateSuccess = accountDAO.updateAccountBalance(account.getAccountNumber(), account.getBalance());

            if (!updateSuccess) {
                throw new Exception("Failed to update account balance in database");
            }

            // Create and save transaction record
            Transaction transaction = new Transaction(account.getAccountNumber(), Transaction.Type.WITHDRAWAL, amount, null);
            boolean saveSuccess = transactionDAO.saveTransaction(transaction);

            if (!saveSuccess) {
                throw new Exception("Failed to save transaction record");
            }

            System.out.println("DEBUG: Withdrawal completed successfully");
            showAlert(AlertType.INFORMATION, "Withdrawal Complete",
                    String.format("Withdrew BWP %,.2f from account %s", amount.doubleValue(), account.getAccountNumber()));

            closeTransactionPanel();
            refreshData();

        } catch (UnsupportedOperationException e) {
            showAlert(AlertType.ERROR, "Operation Not Allowed", e.getMessage());
        } catch (IllegalStateException e) {
            showAlert(AlertType.ERROR, "Transaction Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to save withdrawal: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Account getSelectedAccount() {
        return currentlySelectedAccount;
    }

    @FXML
    private void handleViewAllTransactions() {
        try {
            List<Transaction> allTransactions = transactionDAO.findTransactionsByCustomerId(currentCustomer.getCustomerId());
            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(allTransactions);
            transactionsTable.setItems(transactionList);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Data Error", "Failed to load all transactions: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterTransactions() {
        String filter = transactionFilterComboBox.getValue();
        try {
            List<Transaction> filteredTransactions;
            switch (filter) {
                case "Last 7 Days":
                    filteredTransactions = transactionDAO.findTransactionsByCustomerIdAndPeriod(currentCustomer.getCustomerId(), 7);
                    break;
                case "Last 30 Days":
                    filteredTransactions = transactionDAO.findTransactionsByCustomerIdAndPeriod(currentCustomer.getCustomerId(), 30);
                    break;
                default:
                    filteredTransactions = transactionDAO.findTransactionsByCustomerId(currentCustomer.getCustomerId());
                    break;
            }
            transactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Filter Error", "Failed to filter transactions: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadAccounts();
        loadRecentTransactions();
        loadCustomerData();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    SceneNavigator.toLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Navigation Error", "Unable to logout: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleTermsAndConditions() {
        showAlert(AlertType.INFORMATION, "Terms and Conditions", "All operations are governed by Sediba Financial policies.");
    }

    @FXML
    private void handleCloseTransaction() {
        closeTransactionPanel();
    }

    @FXML
    private void handleTransfer() {
        try {
            List<Account> accounts = accountDAO.findAccountsByCustomerId(currentCustomer.getCustomerId());
            ObservableList<String> accountNumbers = FXCollections.observableArrayList();
            for (Account account : accounts) {
                accountNumbers.add(account.getAccountNumber() + " - " + getAccountTypeDisplay(account));
            }
            transferAccountComboBox.setItems(accountNumbers);
            transactionTitle.setText("Transfer Funds");
            transferSection.setVisible(true);
            openTransactionPanel();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Transfer Error", "Failed to load accounts for transfer: " + e.getMessage());
        }
    }

    private void openTransactionPanel() {
        welcomePanel.setVisible(false);
        welcomePanel.setManaged(false);
        transactionPanel.setVisible(true);
        transactionPanel.setManaged(true);
    }

    private void closeTransactionPanel() {
        transactionPanel.setVisible(false);
        transactionPanel.setManaged(false);
        welcomePanel.setVisible(true);
        welcomePanel.setManaged(true);
        clearTransactionForm();
    }

    private void clearTransactionForm() {
        amountField.clear();
        descriptionField.clear();
        transferAccountComboBox.getSelectionModel().clearSelection();
        transferSection.setVisible(false);
    }

    private void refreshData() {
        loadAccounts();
        loadRecentTransactions();
        loadCustomerData();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 