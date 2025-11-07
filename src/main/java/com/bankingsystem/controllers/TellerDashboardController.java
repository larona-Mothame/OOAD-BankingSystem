package com.bankingsystem.controllers;

import com.bankingsystem.model.Teller;
import com.bankingsystem.util.SceneNavigator;
import com.bankingsystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class TellerDashboardController {

    @FXML private VBox chequingOption;
    @FXML private VBox savingsOption;
    @FXML private VBox investmentOption;
    @FXML private Button proceedButton;
    @FXML private Text usernameHeaderText;

    private String selectedAccountType = null;

    @FXML
    private void initialize() {
        // Display current teller's full name
        displayCurrentTeller();
    }

    private void displayCurrentTeller() {
        try {
            Object currentUser = SessionManager.getCurrentUser();
            if (currentUser instanceof Teller) {
                Teller teller = (Teller) currentUser;
                usernameHeaderText.setText(teller.getFullName());
                System.out.println("DEBUG: Teller dashboard loaded for: " + teller.getFullName() + " (" + teller.getUsername() + ")");
            } else {
                usernameHeaderText.setText("Teller");
                System.err.println("WARNING: Current user is not a Teller object");
            }
        } catch (Exception e) {
            usernameHeaderText.setText("Teller");
            System.err.println("ERROR loading teller information: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openChequingAccount(MouseEvent event) {
        selectAccount("Chequing", chequingOption);
    }

    @FXML
    private void openSavingsAccount(MouseEvent event) {
        selectAccount("Savings", savingsOption);
    }

    @FXML
    private void openInvestmentAccount(MouseEvent event) {
        selectAccount("Investment", investmentOption);
    }


    @FXML
    private void proceedAccountSelection() {
        if (selectedAccountType == null) {
            System.out.println("No account selected!");
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an account type to proceed.");
            return;
        }

        try {
            System.out.println("DEBUG: Proceeding with account type: " + selectedAccountType);


            SessionManager.setAttribute("selectedAccountType", selectedAccountType);

            SceneNavigator.toOpenAccount();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to open account creation: " + e.getMessage());
        }
    }

    @FXML
    private void manageCustomer() {
        try {
            System.out.println("DEBUG: Navigating to customer management");
            SceneNavigator.toCustomerManagement();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to open customer management: " + e.getMessage());
        }
    }

    private void selectAccount(String type, VBox selectedBox) {
        // Reset all option styles
        chequingOption.setStyle(defaultStyle());
        savingsOption.setStyle(defaultStyle());
        investmentOption.setStyle(defaultStyle());

        // Highlight selected one
        selectedBox.setStyle(selectedStyle());
        selectedAccountType = type;

        // Enable proceed button
        proceedButton.setDisable(false);

        System.out.println("DEBUG: Selected account type: " + type);
    }

    private String defaultStyle() {
        return "-fx-padding: 25 35; -fx-background-color: white; -fx-background-radius: 12;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);";
    }

    private String selectedStyle() {
        return "-fx-padding: 25 35; -fx-background-color: #e8f5e9; -fx-background-radius: 12;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,128,0,0.4), 10, 0, 0, 2);";
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Object currentUser = SessionManager.getCurrentUser();
                    String name = "Teller";
                    if (currentUser instanceof Teller) {
                        name = ((Teller) currentUser).getFullName();
                    }
                    System.out.println("DEBUG: Logging out teller: " + name);
                    SessionManager.clearSession();
                    SceneNavigator.toLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to logout: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}