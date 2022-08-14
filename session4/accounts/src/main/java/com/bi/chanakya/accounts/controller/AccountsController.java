package com.bi.chanakya.accounts.controller;

import com.bi.chanakya.accounts.model.Accounts;
import com.bi.chanakya.accounts.model.Customer;
import com.bi.chanakya.accounts.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accountsRepository;

    @GetMapping("/account/details")
    public List<Accounts> getAllAccountDetails() {

        List<Accounts> accounts = accountsRepository.findAll();
        if (!accounts.isEmpty()) {
            return accounts;
        } else {
            return null;
        }

    }

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        if (accounts != null) {
            return accounts;
        } else {
            return null;
        }

    }

}
