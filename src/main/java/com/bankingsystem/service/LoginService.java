package com.bankingsystem.service;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.Teller;
import com.bankingsystem.db.CustomerDAO;
import com.bankingsystem.db.TellerDAO;

public class LoginService {
    private final TellerDAO tellerDAO;
    private final CustomerDAO customerDAO;

    public LoginService() {
        this.tellerDAO = new TellerDAO();
        this.customerDAO = new CustomerDAO();
    }

    public Object authenticate(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return null;
        }

        // First check if it's a teller
        Teller teller = tellerDAO.findByUsernameAndPassword(username, password);
        if (teller != null && teller.isActive()) {
            return teller;
        }

        // Then check if it's a customer
        Customer customer = customerDAO.findByUsernameAndPassword(username, password);
        if (customer != null && customer.isActive()) {
            return customer;
        }

        return null;
    }
}