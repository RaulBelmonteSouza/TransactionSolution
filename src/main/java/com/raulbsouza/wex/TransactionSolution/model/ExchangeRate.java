package com.raulbsouza.wex.TransactionSolution.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRate {
    private LocalDate recordDate;

    private String country;

    private String currency;

    private String countryCurrencyDesc;

    private BigDecimal exchangeRate;

    private LocalDate effectiveDate;

    private String sourceLineNumber;

    private Year fiscalYear;

    private FiscalQuarter fiscalQuarter;

    private Month calendarMonth;

    private int calendarDayNumber;

}
