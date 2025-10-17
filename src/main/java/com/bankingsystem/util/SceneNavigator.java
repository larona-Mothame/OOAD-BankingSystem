package com.bankingsystem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Handles navigation between different scenes (views) in the application.
 */
public class SceneNavigator {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    private static void switchScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(title);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // Navigation routes
    public static void toLogin() throws IOException {
        switchScene("/fxml/Login.fxml", "Sediba Financial - Login");
    }

    public static void toTellerDashboard() throws IOException {
        switchScene("/fxml/TellerDashboard.fxml", "Sediba Financial - Teller Dashboard");
    }

    public static void toCustomerDashboard() throws IOException {
        switchScene("/fxml/CustomerDashboard.fxml", "Sediba Financial - Customer Dashboard");
    }

    public static void toAccountManagement() throws IOException {
        switchScene("/fxml/AccountManagement.fxml", "Sediba Financial - Account Management");
    }

    public static void toAccountLookup() throws IOException {
        switchScene("/fxml/AccountLookup.fxml", "Sediba Financial - Account Lookup");
    }

    public static void toOpenAccount() throws IOException {
        switchScene("/fxml/OpenAccount.fxml", "Sediba Financial - Open New Account");
    }

    public static void logout() throws IOException {
        SessionManager.clearSession();
        toLogin();
    }
}
