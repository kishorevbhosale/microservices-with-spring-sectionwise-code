package com.bi.chanakya.accounts.controller;

import com.bi.chanakya.accounts.client.CardsFeignClient;
import com.bi.chanakya.accounts.client.LoansFeignClient;
import com.bi.chanakya.accounts.config.AccountServiceConfig;
import com.bi.chanakya.accounts.model.*;
import com.bi.chanakya.accounts.repository.AccountsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AccountsController {
    @Autowired
    LoansFeignClient loansFeignClient;
    @Autowired
    CardsFeignClient cardsFeignClient;
    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    AccountServiceConfig accountConfig;

    @PostMapping("/account/details")
    @Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
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

    @PostMapping("/customerdetails")
    //@CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod ="myCustomerDetailsFallBack")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetails(@RequestHeader("skbank-correlation-id") String correlationid, @RequestBody Customer customer) {
        log.info("myCustomerDetails() method started");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(correlationid, customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);
        log.info("myCustomerDetails() method ended");
        return customerDetails;
    }

    private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("skbank-correlation-id") String correlationid, Customer customer, Throwable t) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        return customerDetails;
    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome to SKBank";
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi, Welcome to SKBank";
    }
}
