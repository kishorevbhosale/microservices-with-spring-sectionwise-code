package com.bi.chanakya.loans.controller;

import com.bi.chanakya.loans.config.LoanServiceConfig;
import com.bi.chanakya.loans.model.Customer;
import com.bi.chanakya.loans.model.Loans;
import com.bi.chanakya.loans.model.Properties;
import com.bi.chanakya.loans.repository.LoansRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class LoansController {

    private final LoansRepository loansRepository;
    private final LoanServiceConfig loanServiceConfig;

    @PostMapping("/loans")
    public List<Loans> getLoansDetails(@RequestHeader("skbank-correlation-id") String correlationid, @RequestBody Customer customer) {
        log.info("Invoking loan service");
        return loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
    }

    @GetMapping("/loans/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(loanServiceConfig.getMsg(),
                loanServiceConfig.getBuildVersion(),
                loanServiceConfig.getMailDetails(),
                loanServiceConfig.getActiveBranches());
        return objectWriter.writeValueAsString(properties);
    }

}
