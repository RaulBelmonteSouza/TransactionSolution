package com.raulbsouza.wex.TransactionSolution.testutils;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDTO;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestUtils {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;

    public static final String FAKE_CURRENCY = "Fake-Currency";

    public static BigDecimal randomAmount() {
        int range = 1000;
        return new BigDecimal(Math.random() * range)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static ExchangeRateDataDTO getExchangeRateDataDto(LocalDate date) {
        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(FAKE_CURRENCY)
                .exchangeRate(randomAmount().toString())
                .recordDate(date.minusMonths(1))
                .build();

        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(FAKE_CURRENCY)
                .exchangeRate(randomAmount().toString())
                .recordDate(date.minusMonths(2))
                .build();

        return new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));
    }
}
