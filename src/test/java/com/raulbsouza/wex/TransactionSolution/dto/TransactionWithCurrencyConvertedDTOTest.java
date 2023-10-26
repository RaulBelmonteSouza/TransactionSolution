package com.raulbsouza.wex.TransactionSolution.dto;

import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateFields;
import com.raulbsouza.wex.TransactionSolution.model.ExchangeRate;
import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionWithCurrencyConvertedDTOTest {

    @Test
    void givenTransactionAndExchangeRate_shouldGenerateDto() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .description("Transaction 1")
                .amount(TestUtils.randomAmount())
                .date(LocalDate.now())
                .build();

        BigDecimal expectedAmount = transaction.getAmount()
                .multiply(BigDecimal.TEN)
                .setScale(2, RoundingMode.HALF_UP);

        LocalDate date = LocalDate.now();
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setRecordDate(date);
        dto.setCountry("Brazil");
        dto.setCurrency("Real");
        dto.setCountryCurrencyDesc("Brazil-Real");
        dto.setExchangeRate(BigDecimal.TEN.toString());
        dto.setEffectiveDate(date);
        dto.setSourceLineNumber("1");
        dto.setFiscalYear(null);
        dto.setFiscalQuarter(null);
        dto.setCalendarMonth(null);
        dto.setCalendarDayNumber(null);

        ExchangeRate exchangeRate = dto.toEntity();

        BigDecimal convertedAmount = transaction.getAmount()
                .multiply(exchangeRate.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);

        TransactionWithCurrencyConvertedDTO transactionWithCurrencyConvertedDTO = TransactionWithCurrencyConvertedDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .transactionDate(transaction.getDate())
                .originalCurrency(ExchangeRateFields.DEFAULT_CURRENCY)
                .originalAmount(transaction.getAmount())
                .convertedAmount(convertedAmount)
                .conversionCurrency(exchangeRate.getCurrency())
                .exchangeRate(exchangeRate.getExchangeRate())
                .build();

        assertEquals(transaction.getId(), transactionWithCurrencyConvertedDTO.getId());
        assertEquals(transaction.getDescription(), transactionWithCurrencyConvertedDTO.getDescription());
        assertEquals(transaction.getDate(), transactionWithCurrencyConvertedDTO.getTransactionDate());
        assertEquals(transaction.getAmount(), transactionWithCurrencyConvertedDTO.getOriginalAmount());
        assertEquals(expectedAmount, transactionWithCurrencyConvertedDTO.getConvertedAmount());
        assertEquals(exchangeRate.getCurrency(), transactionWithCurrencyConvertedDTO.getConversionCurrency());
        assertEquals(exchangeRate.getExchangeRate(), transactionWithCurrencyConvertedDTO.getExchangeRate());
    }
}
