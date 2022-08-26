package com.bi.chanakya.accounts.client;

import com.bi.chanakya.accounts.model.Cards;
import com.bi.chanakya.accounts.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("cards")
public interface CardsFeignClient {

    @PostMapping(value = "cards", consumes = "application/json")
    List<Cards> getCardDetails(@RequestHeader("skbank-correlation-id") String correlationid, @RequestBody Customer customer);
}
