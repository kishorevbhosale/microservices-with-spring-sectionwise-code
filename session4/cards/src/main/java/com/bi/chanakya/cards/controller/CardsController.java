package com.bi.chanakya.cards.controller;

import com.bi.chanakya.cards.config.CardsServiceConfig;
import com.bi.chanakya.cards.model.Cards;
import com.bi.chanakya.cards.model.Customer;
import com.bi.chanakya.cards.model.Properties;
import com.bi.chanakya.cards.repository.CardsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CardsController {
    private final CardsRepository cardsRepository;
    private final CardsServiceConfig cardsServiceConfig;

    @PostMapping("/cards")
    public List<Cards> getCardDetails(@RequestBody Customer customer) {
        return cardsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/cards/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(cardsServiceConfig.getMsg(), cardsServiceConfig.getBuildVersion(),
                cardsServiceConfig.getMailDetails(), cardsServiceConfig.getActiveBranches());
        return objectWriter.writeValueAsString(properties);
    }

}
