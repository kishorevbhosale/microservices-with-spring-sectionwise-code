package com.bi.chanakya.accounts.client;

import com.bi.chanakya.accounts.model.Customer;
import com.bi.chanakya.accounts.model.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("loans")
public interface LoansFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "loans", consumes = "application/json")
    List<Loans> getLoansDetails(@RequestBody Customer customer);
}
