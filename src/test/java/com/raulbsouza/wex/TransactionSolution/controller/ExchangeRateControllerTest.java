package com.raulbsouza.wex.TransactionSolution.controller;

import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateClient;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDTO;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.service.ExchangeRateService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDate;
import java.util.List;

import static com.raulbsouza.wex.TransactionSolution.testutils.TestUtils.DATE_FORMAT;
import static com.raulbsouza.wex.TransactionSolution.testutils.TestUtils.randomAmount;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeRateControllerTest {

    private static final String EXCHANGE_RATES_PATH = "/exchange-rates";
    private static final String LIST_VALID_CURRENCIES_ENDPOINT = "/countries";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateClient exchangeRateClient;

    @SpyBean
    private ExchangeRateService exchangeRateService;

    @Test
    void shouldFetchAllValidCurrencies() throws Exception {

        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder().countryCurrencyDesc("Fake-Currency1").build();
        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder().countryCurrencyDesc("Fake-Currency2").build();

        ExchangeRateDataDTO exchangeRateDataDTO = new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));

        doReturn(exchangeRateDataDTO).when(exchangeRateClient)
                .getValidCurrencies(ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String.class));

        mockMvc.perform(get(EXCHANGE_RATES_PATH + LIST_VALID_CURRENCIES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", Is.is(2)));
    }

    @Test
    void givenClientFailure_whenFetchAllValidCurrencies_thenInternalServerError() throws Exception {
        doThrow(new UndeclaredThrowableException(new Exception("Error")))
                .when(exchangeRateService).retrieveExchangeRateValidCurrencies();

        mockMvc.perform(get(EXCHANGE_RATES_PATH + LIST_VALID_CURRENCIES_ENDPOINT))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGetExchangeForCurrency() throws Exception {
        String currency = "Fake-Currency";

        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(currency)
                .exchangeRate(randomAmount().toString())
                .recordDate(LocalDate.now().minusDays(2))
                .build();

        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder()
                .countryCurrencyDesc(currency)
                .exchangeRate(randomAmount().toString())
                .recordDate(LocalDate.now().minusDays(3))
                .build();


        ExchangeRateDataDTO exchangeRateDataDTO = new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));

        doReturn(exchangeRateDataDTO).when(exchangeRateClient)
                .getExchangeHistoryForCountry(ArgumentMatchers.any(String[].class),
                        ArgumentMatchers.any(String.class),
                        ArgumentMatchers.any(String.class),
                        ArgumentMatchers.any(String.class));

        mockMvc.perform(get(EXCHANGE_RATES_PATH)
                        .param("currency", currency)
                        .param("date", LocalDate.now().format(DATE_FORMAT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", Is.is(2)));
    }


    @Test
    void givenClientFailure_whenGetExchangeForCurrency_thenInternalServerError() throws Exception {
        doThrow(new UndeclaredThrowableException(new Exception("Error")))
                .when(exchangeRateService).retrieveExchangeRateValidCurrencies();

        mockMvc.perform(get(EXCHANGE_RATES_PATH))
                .andExpect(status().isInternalServerError());
    }

}
