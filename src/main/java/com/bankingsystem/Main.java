package com.bankingsystem;

import com.bankingsystem.db.AccountDAO;
import com.bankingsystem.util.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private final AccountDAO accountDAO = new AccountDAO(); // your DAO
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneNavigator.setStage(primaryStage);
        SceneNavigator.toLogin();


//        startAutomaticInterestApplication();
    }

    private void startAutomaticInterestApplication() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                var accounts = accountDAO.findAllAccountsWithCustomerInfo()
                        .stream()
                        .map(a -> accountDAO.findAccountByNumber(a.getAccountNumber()))
                        .filter(a -> a instanceof com.bankingsystem.model.ApplyInterest)
                        .toList();

                for (var account : accounts) {
                    var interestAccount = (com.bankingsystem.model.ApplyInterest) account;
                    var interest = interestAccount.applyInterest();
                    accountDAO.updateAccountBalance(account.getAccountNumber(), account.getBalance());
                    System.out.println("Applied " + interest + " to " + account.getAccountNumber());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.DAYS); // every 30 days
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduler.shutdownNow(); // stop scheduler on app exit
    }

    public static void main(String[] args) {
        launch(args);
    }
}
