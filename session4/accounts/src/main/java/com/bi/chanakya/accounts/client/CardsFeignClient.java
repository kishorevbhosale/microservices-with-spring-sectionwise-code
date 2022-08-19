package com.bi.chanakya.accounts.client;

import com.bi.chanakya.accounts.model.Cards;
import com.bi.chanakya.accounts.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("cards")
public interface CardsFeignClient {
    @RequestMapping(method = RequestMethod.POST, value = "cards", consumes = "application/json")
    List<Cards> getCardDetails(@RequestBody Customer customer);
}
