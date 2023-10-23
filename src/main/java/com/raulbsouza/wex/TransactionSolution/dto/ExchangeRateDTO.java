package com.raulbsouza.wex.TransactionSolution.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateFields;
import com.raulbsouza.wex.TransactionSolution.model.ExchangeRate;
import com.raulbsouza.wex.TransactionSolution.model.FiscalQuarter;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateDTO {

    @JsonProperty(ExchangeRateFields.RECORD_DATE)
    private LocalDate recordDate;

    @JsonProperty(ExchangeRateFields.COUNTRY)
    private String country;

    @JsonProperty(ExchangeRateFields.CURRENCY)
    private String currency;

    @JsonProperty(ExchangeRateFields.COUNTRY_CURRENCY_DESC)
    private String countryCurrencyDesc;

    @JsonProperty(ExchangeRateFields.EXCHANGE_RATE)
    private String exchangeRate;

    @JsonProperty(ExchangeRateFields.EFFECTIVE_DATE)
    private LocalDate effectiveDate;

    @JsonProperty(ExchangeRateFields.SRC_LINE_NBR)
    private String sourceLineNumber;

    @JsonProperty(ExchangeRateFields.RECORD_FISCAL_YEAR)
    private String fiscalYear;

    @JsonProperty(ExchangeRateFields.RECORD_FISCAL_QUARTER)
    private String fiscalQuarter;

    @JsonProperty(ExchangeRateFields.RECORD_CALENDAR_MONTH)
    private String calendarMonth;

    @JsonProperty(ExchangeRateFields.RECORD_CALENDAR_DAY)
    private String calendarDayNumber;

    public ExchangeRate toEntity() {
        return ExchangeRate.builder()
                .recordDate(this.recordDate)
                .country(this.country)
                .currency(this.currency)
                .countryCurrencyDesc(this.countryCurrencyDesc)
                .exchangeRate(new BigDecimal(this.exchangeRate))
                .effectiveDate(this.effectiveDate)
                .sourceLineNumber(this.sourceLineNumber)
                .fiscalYear(Year.parse(this.fiscalYear))
                .fiscalQuarter(FiscalQuarter.valueOf(this.fiscalQuarter))
                .calendarMonth(Month.of(Integer.parseInt(this.calendarMonth)))
                .calendarDayNumber(Integer.parseInt(this.calendarDayNumber))
                .build();
    }
}
