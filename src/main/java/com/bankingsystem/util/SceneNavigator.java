package com.bankingsystem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.io.InputStream;

/**
 * Handles navigation between different scenes (views) in the application.
 */
public class SceneNavigator {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }
    private static void switchScene(String fxmlPath, String title) throws IOException {
        System.out.println("Looking for FXML at: " + fxmlPath);

        URL fxmlUrl = null;

        // Try the normal path first
        fxmlUrl = SceneNavigator.class.getClassLoader().getResource(fxmlPath);

        // If not found and we're looking in fxml/, try with leading space
        if (fxmlUrl == null && fxmlPath.startsWith("fxml/")) {
            String filename = fxmlPath.substring(5); // Remove "fxml/"
            String alternativePath = "fxml/ " + filename; // Add space after fxml/
            System.out.println("Trying alternative path with space: '" + alternativePath + "'");
            fxmlUrl = SceneNavigator.class.getClassLoader().getResource(alternativePath);
        }

        // Last resort: try direct file access
        if (fxmlUrl == null) {
            try {
                java.nio.file.Path directPath = java.nio.file.Paths.get("target/classes/" + fxmlPath);
                if (java.nio.file.Files.exists(directPath)) {
                    System.out.println("Found via direct file path: " + directPath.toAbsolutePath());
                    fxmlUrl = directPath.toUri().toURL();
                }
            } catch (Exception e) {
                System.err.println("Direct file loading also failed: " + e.getMessage());
            }
        }

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        System.out.println("Successfully loaded FXML from: " + fxmlUrl);
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(title);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // Navigation routes
    public static void toLogin() throws IOException {
        switchScene("/fxml/LoginUI.fxml", "Sediba Financial - Login");
    }

    public static void toTellerDashboard() throws IOException {
        switchScene("/fxml/TellerDashboard.fxml", "Sediba Financial - Teller Dashboard");
    }

    public static void toCustomerDashboard() throws IOException {
        switchScene("/fxml/AccountManagement.fxml", "Sediba Financial - Customer Dashboard");
    }

    public static void toAccountManagement() throws IOException {
        switchScene("/fxml/AccountManagement.fxml", "Sediba Financial - Account Management");
    }

    public static void toAccountLookup() throws IOException {
        switchScene("/fxml/AccountLookup.fxml", "Sediba Financial - Account Lookup");
    }

    public static void toOpenAccount() throws IOException {
        switchScene("/fxml/AccountOpening.fxml", "Sediba Financial - Open New Account");
    }

    public static void toCustomerManagement() throws IOException {
        switchScene("/fxml/CustomerMangement.fxml", "Sediba Financial - Customer Management");
    }

    public static void totransaction_history() throws IOException {
        switchScene("/fxml/transaction_history_view.fxml", "Sediba Financial - Transaction History");
    }

    public static void toTransaction() throws IOException {
        switchScene("/fxml/transaction_view.fxml", "Sediba Financial - Transaction");
    }

    public static void logout() throws IOException {
        SessionManager.clearSession();
        toLogin();
    }
}
