package com.bankingsystem;

import com.bankingsystem.util.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneNavigator.setStage(primaryStage);
        SceneNavigator.totransaction_history();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
