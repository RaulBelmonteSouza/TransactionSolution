package com.raulbsouza.wex.TransactionSolution.service;

import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateClient;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDTO;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDate;
import java.util.List;

import static com.raulbsouza.wex.TransactionSolution.testutils.TestUtils.FAKE_CURRENCY;
import static com.raulbsouza.wex.TransactionSolution.testutils.TestUtils.randomAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class ExchangeRateServiceTest {

    @SpyBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private ExchangeRateClient exchangeRateClient;

    @Test
    void retrieveExchangeRateValidCurrencies_Successfully() {
        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder().countryCurrencyDesc(FAKE_CURRENCY).build();
        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder().countryCurrencyDesc(FAKE_CURRENCY).build();

        ExchangeRateDataDTO exchangeRateDataDTO = new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));

        doReturn(exchangeRateDataDTO).when(exchangeRateClient)
                .getValidCurrencies(ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class));

        ExchangeRateDataDTO resultExchangeRateDataDTO = exchangeRateService.retrieveExchangeRateValidCurrencies();

        assertEquals(resultExchangeRateDataDTO, exchangeRateDataDTO);
    }

    @Test
    void givenClientFailure_whenRetrieveExchangeRateValidCurrencies_thenUndeclaredThrowableException() {
        doThrow(new UndeclaredThrowableException(new Exception("Error")))
                .when(exchangeRateClient).getValidCurrencies(any(String[].class), any(String.class));

        assertThrows(UndeclaredThrowableException.class,
                () -> exchangeRateService.retrieveExchangeRateValidCurrencies());
    }

    @Test
    void retrieveExchangeForCurrency_Successfully() {
        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(FAKE_CURRENCY)
                .exchangeRate(randomAmount().toString())
                .recordDate(LocalDate.now().minusDays(2))
                .build();

        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(FAKE_CURRENCY)
                .exchangeRate(randomAmount().toString())
                .recordDate(LocalDate.now().minusDays(3))
                .build();


        ExchangeRateDataDTO exchangeRateDataDTO = new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));

        doReturn(exchangeRateDataDTO).when(exchangeRateClient)
                .getExchangeHistoryForCountry(any(String[].class),
                        any(String.class),
                        any(String.class),
                        any(String.class));

        ExchangeRateDataDTO resultExchangeRateDataDTO =
                exchangeRateService.retrieveExchangeForCurrency(FAKE_CURRENCY, LocalDate.now());

        assertEquals(resultExchangeRateDataDTO, exchangeRateDataDTO);
    }

    @Test
    void givenClientFailure_whenRetrieveExchangeForCurrency_thenUndeclaredThrowableException() {
        doThrow(new UndeclaredThrowableException(new Exception("Error"))).when(exchangeRateClient)
                .getExchangeHistoryForCountry(ArgumentMatchers.any(String[].class),
                        ArgumentMatchers.any(String.class),
                        ArgumentMatchers.any(String.class),
                        ArgumentMatchers.any(String.class));

        assertThrows(UndeclaredThrowableException.class,
                () -> exchangeRateService.retrieveExchangeForCurrency(FAKE_CURRENCY, LocalDate.now()));
    }
}