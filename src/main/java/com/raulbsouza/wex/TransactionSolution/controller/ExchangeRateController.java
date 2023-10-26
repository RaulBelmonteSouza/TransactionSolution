package com.raulbsouza.wex.TransactionSolution.controller;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "exchange-rates", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Operation(summary = "List all valid country currency for exchange rate.",
            description = "Null fields in the response will be ignored.")
    @GetMapping("/countries")
    public ExchangeRateDataDTO getValidCountryCurrencies() {
        return exchangeRateService.retrieveExchangeRateValidCurrencies();
    }

    @Operation(summary = "Get exchange information for specified currency from the date informed " +
            "within the last 6 months.", description = "Null fields in the response will be ignored.")
    @ApiResponse(responseCode = "200")
    @GetMapping
    public ExchangeRateDataDTO getExchangeForCurrency(@RequestParam String currency, @RequestParam LocalDate date) {
        return exchangeRateService.retrieveExchangeForCurrency(currency, date);
    }
}
