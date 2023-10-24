package com.raulbsouza.wex.TransactionSolution.service;

import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateClient;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.raulbsouza.wex.TransactionSolution.client.ExchangeRateFields.*;

@Service
public class ExchangeRateService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;

    @Autowired
    private ExchangeRateClient exchangeRateClient;

    public ExchangeRateDataDTO retrieveExchangeRateValidCurrencies() {
        String[] fields = { COUNTRY_CURRENCY_DESC };
        String dateParam = LocalDate.now().minusYears(1).format(DATE_FORMAT);
        return exchangeRateClient.getValidCurrencies(fields, dateParam);
    }

    public ExchangeRateDataDTO retrieveExchangeForCurrency(String currency, LocalDate date) {
        String startDate = date.minusMonths(6).format(DATE_FORMAT);
        String endDate = date.format(DATE_FORMAT);
        String[] fields = { COUNTRY_CURRENCY_DESC, EXCHANGE_RATE, RECORD_DATE };

        return exchangeRateClient.getExchangeHistoryForCountry(fields, currency, startDate, endDate);
    }
}
