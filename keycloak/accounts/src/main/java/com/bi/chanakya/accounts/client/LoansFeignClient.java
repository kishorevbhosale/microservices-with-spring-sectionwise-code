package com.bi.chanakya.accounts.client;

import com.bi.chanakya.accounts.model.Customer;
import com.bi.chanakya.accounts.model.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("loans")
public interface LoansFeignClient {

    @PostMapping(value = "loans", consumes = "application/json")
    List<Loans> getLoansDetails(@RequestHeader("skbank-correlation-id") String correlationid, @RequestBody Customer customer);
}
