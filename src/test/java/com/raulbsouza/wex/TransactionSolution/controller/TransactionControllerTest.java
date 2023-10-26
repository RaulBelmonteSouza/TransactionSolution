package com.raulbsouza.wex.TransactionSolution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateClient;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDTO;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionDTO;
import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.repository.TransactionRepository;
import com.raulbsouza.wex.TransactionSolution.testutils.TestUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.raulbsouza.wex.TransactionSolution.client.ExchangeRateFields.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    private static final String TRANSACTION_PATH = "/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExchangeRateClient client;

    @SpyBean
    private TransactionRepository transactionRepository;

    @AfterEach
    void afterEach() {
        transactionRepository.deleteAll();
    }

    @Test
    void shouldFetchAllTransactions() throws Exception {
        Transaction transaction1 = Transaction.builder()
                .date(LocalDate.now().minusMonths(1))
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        Transaction transaction2 = Transaction.builder()
                .date(LocalDate.now().minusMonths(2))
                .description("Transaction 2")
                .amount(TestUtils.randomAmount()).build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        this.mockMvc.perform(get(TRANSACTION_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount", Is.is(transaction1.getAmount().doubleValue())))
                .andExpect(jsonPath("$.content[0].description", Is.is(transaction1.getDescription())))
                .andExpect(jsonPath("$.content[0].date", Is.is(transaction1.getDate().toString())))
                .andExpect(jsonPath("$.content[1].amount", Is.is(transaction2.getAmount().doubleValue())))
                .andExpect(jsonPath("$.content[1].description", Is.is(transaction2.getDescription())))
                .andExpect(jsonPath("$.content[1].date", Is.is(transaction2.getDate().toString())));
    }

    @Test
    void shouldFetchAllTransactions_sortedByDate() throws Exception {
        Transaction transaction1 = Transaction.builder()
                .date(LocalDate.now().minusMonths(1))
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        Transaction transaction2 = Transaction.builder()
                .date(LocalDate.now().minusMonths(2))
                .description("Transaction 2")
                .amount(TestUtils.randomAmount()).build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        this.mockMvc.perform(get(TRANSACTION_PATH).param("sort", "date,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].date", Is.is(transaction2.getDate().toString())))
                .andExpect(jsonPath("$.content[1].date", Is.is(transaction1.getDate().toString())));
    }

    @Test
    void whenFetchPaginated_then200IsReceived() throws Exception {
        this.mockMvc.perform(get(TRANSACTION_PATH)
                        .param("page", "1")
                        .param("size", "23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", Is.is(1)))
                .andExpect(jsonPath("$.size", Is.is(23)));
    }

    @Test
    void shouldFetchTransactionsFilterByDate() throws Exception {
        Transaction transaction1 = Transaction.builder()
                .date(LocalDate.now().minusMonths(1))
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        Transaction transaction2 = Transaction.builder()
                .date(LocalDate.now().minusMonths(2))
                .description("Transaction 2")
                .amount(TestUtils.randomAmount()).build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        String endDate = transaction2.getDate().plusDays(1).format(DATE_FORMAT);
        String startDate = transaction2.getDate().minusDays(10).format(DATE_FORMAT);

        this.mockMvc.perform(get(TRANSACTION_PATH)
                        .param("dateStart", startDate)
                        .param("dateEnd", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", Is.is(1)))
                .andExpect(jsonPath("$.content[0].date", Is.is(transaction2.getDate().toString())));
    }

    @Test
    void shouldGetTransactionByIdAndCurrency() throws Exception {
        LocalDate date = LocalDate.now().minusMonths(1);
        String currency = "Fake-Currency";

        Transaction transaction = Transaction.builder()
                .date(date)
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        Transaction transactionEntity = transactionRepository.save(transaction);

        String startDate = date.minusMonths(6).format(DATE_FORMAT);
        String endDate = date.format(DATE_FORMAT);
        String[] fields = { COUNTRY_CURRENCY_DESC, EXCHANGE_RATE, RECORD_DATE };

        doReturn(getExchangeRateDataDto(date))
                .when(client)
                .getExchangeHistoryForCountry(fields, currency, startDate, endDate);

        this.mockMvc.perform(get(TRANSACTION_PATH + "/{id}", transactionEntity.getId())
                .param("currency", currency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversionCurrency", Is.is(currency)));
    }

    @Test
    void givenEmptyExchangeData_whenGetTransactionByIdAndCurrency_thenBadRequest() throws Exception {
        LocalDate date = LocalDate.now().minusMonths(1);
        String currency = "Fake-Currency";

        Transaction transaction = Transaction.builder()
                .date(date)
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        Transaction transactionEntity = transactionRepository.save(transaction);

        String startDate = date.minusMonths(6).format(DATE_FORMAT);
        String endDate = date.format(DATE_FORMAT);
        String[] fields = { COUNTRY_CURRENCY_DESC, EXCHANGE_RATE, RECORD_DATE };

        doReturn(new ExchangeRateDataDTO(List.of()))
                .when(client)
                .getExchangeHistoryForCountry(fields, currency, startDate, endDate);

        this.mockMvc.perform(get(TRANSACTION_PATH + "/{id}", transactionEntity.getId())
                        .param("currency", currency))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message",
                                Is.is("The purchase cannot be converted to the target currency.")));
    }

    @Test
    void givenNonExistentTransaction_whenGetTransactionByIdAndCurrency_thenNotFound()
            throws Exception {
        String currency = "Fake-Currency";
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.now())
                .description("Transaction 1")
                .amount(TestUtils.randomAmount()).build();

        this.mockMvc.perform(get(TRANSACTION_PATH + "/{id}", transaction.getId())
                        .param("currency", currency))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Is.is("Transaction not found")));
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .description("Transaction")
                .amount(TestUtils.randomAmount())
                .date(LocalDate.now().minusMonths(1))
                .build();

        this.mockMvc.perform(post(TRANSACTION_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void givenEmptyFieldsInDto_whenCreateTransaction_thenBadRequest() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .description("Transaction")
                .amount(null)
                .date(null)
                .build();

        this.mockMvc.perform(post(TRANSACTION_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations.length()", Is.is(2)))
                .andExpect(content()
                        .string(both(containsString("must not be null"))
                                .and(containsString("Date is mandatory"))));
    }

    @Test
    void givenNegativeAmount_whenCreateTransaction_thenBadRequest() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .description("Transaction")
                .amount(BigDecimal.valueOf(-99.99))
                .date(LocalDate.now())
                .build();

        this.mockMvc.perform(post(TRANSACTION_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations.length()", Is.is(1)))
                .andExpect(
                        jsonPath("$.violations[0].message", Is.is("Amount must be a positive number")));
    }

    private ExchangeRateDataDTO getExchangeRateDataDto(LocalDate date) {
        ExchangeRateDTO exchangeRateDTO1 = ExchangeRateDTO.builder()
                .countryCurrencyDesc("Fake-Currency")
                .exchangeRate(TestUtils.randomAmount().toString())
                .recordDate(date.minusMonths(1))
                .build();

        ExchangeRateDTO exchangeRateDTO2 = ExchangeRateDTO.builder()
                .countryCurrencyDesc("Fake-Currency2")
                .exchangeRate(TestUtils.randomAmount().toString())
                .recordDate(date.minusMonths(2))
                .build();

        return new ExchangeRateDataDTO(List.of(exchangeRateDTO1, exchangeRateDTO2));
    }



}
