package com.bi.chanakya.accounts.controller;

import com.bi.chanakya.accounts.config.AccountServiceConfig;
import com.bi.chanakya.accounts.model.Accounts;
import com.bi.chanakya.accounts.model.Customer;
import com.bi.chanakya.accounts.model.Properties;
import com.bi.chanakya.accounts.repository.AccountsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountsController {
    private final AccountsRepository accountsRepository;
    private final AccountServiceConfig accountConfig;

    @PostMapping("/account/details")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/account/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountConfig.getMsg(),
                accountConfig.getBuildVersion(),
                accountConfig.getMailDetails(),
                accountConfig.getActiveBranches());
        return objectWriter.writeValueAsString(properties);
    }

}
