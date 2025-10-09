package com.bankingsystem.service;

import com.bankingsystem.model.Customer;
import com.bankingsystem.model.IndividualCustomer;
import com.bankingsystem.model.CompanyCustomer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing customers (creation, lookup, updates).
 * Wraps Bank operations for cleaner controller usage.
 */
public class CustomerService {

    private final BankService bankService = BankService.getInstance();

    /**
     * Register a new individual customer.
     */
    public IndividualCustomer createIndividualCustomer(String name, String contactNumber,
                                                       String email, String address,
                                                       String nationalId, LocalDate dob) {
        IndividualCustomer customer = new IndividualCustomer(name, contactNumber, email, address, nationalId, dob);
        bankService.getBank().addCustomer(customer);
        return customer;
    }

    /**
     * Register a new company customer.
     */
    public CompanyCustomer createCompanyCustomer(String companyName, String regNo,
                                                 String primaryContact, String contactNumber,
                                                 String email, String address) {
        CompanyCustomer customer = new CompanyCustomer(companyName, regNo, primaryContact, contactNumber, email, address);
        bankService.getBank().addCustomer(customer);
        return customer;
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return bankService.getBank().findCustomerById(customerId);
    }

    public List<Customer> searchCustomersByName(String nameFragment) {
        return bankService.getBank().searchCustomersByName(nameFragment);
    }

    // TODO: add updateCustomer(), deleteCustomer() if needed
}
