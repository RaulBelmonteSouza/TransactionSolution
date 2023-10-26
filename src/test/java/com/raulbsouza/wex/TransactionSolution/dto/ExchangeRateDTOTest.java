package com.raulbsouza.wex.TransactionSolution.dto;

import com.raulbsouza.wex.TransactionSolution.model.ExchangeRate;
import com.raulbsouza.wex.TransactionSolution.model.FiscalQuarter;
import com.raulbsouza.wex.TransactionSolution.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ExchangeRateDTOTest {

    @Test
    void shouldConvertDtoToEntity() {
        LocalDate date = LocalDate.now().minusYears(1);
        ExchangeRateDTO dto = ExchangeRateDTO.builder()
                .recordDate(date)
                .country("Brazil")
                .currency("Real")
                .countryCurrencyDesc("Brazil-Real")
                .exchangeRate(TestUtils.randomAmount().toString())
                .effectiveDate(date)
                .sourceLineNumber("1")
                .fiscalYear(String.valueOf(date.getYear()))
                .fiscalQuarter(FiscalQuarter.Q1_OCTOBER_TO_DECEMBER.toString())
                .calendarMonth(String.valueOf(date.getMonthValue()))
                .calendarDayNumber(String.valueOf(date.getDayOfMonth()))
                .build();

        ExchangeRate entity = dto.toEntity();

        assertEquals(dto.getRecordDate(), entity.getRecordDate());
        assertEquals(dto.getCountry(), entity.getCountry());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getCountryCurrencyDesc(), entity.getCountryCurrencyDesc());
        assertEquals(dto.getExchangeRate(), entity.getExchangeRate().toString());
        assertEquals(dto.getEffectiveDate(), entity.getEffectiveDate());
        assertEquals(dto.getSourceLineNumber(), entity.getSourceLineNumber());
        assertEquals(dto.getFiscalYear(), entity.getFiscalYear().toString());
        assertEquals(dto.getFiscalQuarter(), entity.getFiscalQuarter().toString());
        assertEquals(dto.getCalendarMonth(), String.valueOf(entity.getCalendarMonth().getValue()));
        assertEquals(dto.getCalendarDayNumber(), String.valueOf(entity.getCalendarDayNumber()));
    }

    @Test
    void whenNullDateInfo_shouldConvertDtoToEntity() {
        LocalDate date = LocalDate.now().minusYears(1);
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setRecordDate(date);
        dto.setCountry("Brazil");
        dto.setCurrency("Real");
        dto.setCountryCurrencyDesc("Brazil-Real");
        dto.setExchangeRate(TestUtils.randomAmount().toString());
        dto.setEffectiveDate(date);
        dto.setSourceLineNumber("1");
        dto.setFiscalYear(null);
        dto.setFiscalQuarter(null);
        dto.setCalendarMonth(null);
        dto.setCalendarDayNumber(null);

        ExchangeRate entity = dto.toEntity();

        assertEquals(dto.getRecordDate(), entity.getRecordDate());
        assertEquals(dto.getCountry(), entity.getCountry());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getCountryCurrencyDesc(), entity.getCountryCurrencyDesc());
        assertEquals(dto.getExchangeRate(), entity.getExchangeRate().toString());
        assertEquals(dto.getEffectiveDate(), entity.getEffectiveDate());
        assertEquals(dto.getSourceLineNumber(), entity.getSourceLineNumber());
        assertNotNull(entity.getFiscalYear().toString());
        assertNotNull(entity.getCalendarMonth());
        assertNotNull(entity.getCalendarDayNumber());
    }
}
