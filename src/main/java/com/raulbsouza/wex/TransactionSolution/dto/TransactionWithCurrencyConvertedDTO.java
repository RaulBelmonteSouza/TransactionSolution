package com.raulbsouza.wex.TransactionSolution.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionWithCurrencyConvertedDTO {
    private UUID id;

    private String description;

    private LocalDate transactionDate;

    private String originalCurrency;

    private BigDecimal originalAmount;

    private BigDecimal convertedAmount;

    private String conversionCurrency;

    private BigDecimal exchangeRate;
}
