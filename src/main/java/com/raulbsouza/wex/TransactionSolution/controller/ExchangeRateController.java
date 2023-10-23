package com.raulbsouza.wex.TransactionSolution.controller;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/countries")
    public ExchangeRateDataDTO get() {
        return exchangeRateService.retrieveExchangeRateDate();
    }

    @GetMapping
    public ExchangeRateDataDTO getExchangeForCurrency(@RequestParam String currency, @RequestParam LocalDate date) {
        return exchangeRateService.retrieveExchangeForCurrency(currency, date);
    }
}
