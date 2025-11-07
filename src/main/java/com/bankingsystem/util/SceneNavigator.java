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

        fxmlUrl = SceneNavigator.class.getClassLoader().getResource(fxmlPath);


        if (fxmlUrl == null && fxmlPath.startsWith("fxml/")) {
            String filename = fxmlPath.substring(5);
            String alternativePath = "fxml/ " + filename;
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

    public static void toOpenAccount() throws IOException {
        switchScene("/fxml/AccountOpening.fxml", "Sediba Financial - Open New Account");
    }

    public static void toCustomerManagement() throws IOException {
        switchScene("/fxml/CustomerMangement.fxml", "Sediba Financial - Customer Management");
    }

}
