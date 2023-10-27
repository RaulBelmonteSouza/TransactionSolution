package com.raulbsouza.wex.TransactionSolution.service;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionWithCurrencyConvertedDTO;
import com.raulbsouza.wex.TransactionSolution.exception.ExchangeRateClientException;
import com.raulbsouza.wex.TransactionSolution.exception.ResourceNotFoundException;
import com.raulbsouza.wex.TransactionSolution.model.ExchangeRate;
import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.repository.TransactionRepository;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.SearchCriteria;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.TransactionSearchCriteria;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.raulbsouza.wex.TransactionSolution.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
public class TransactionServiceTest {

    @SpyBean
    private TransactionRepository transactionRepository;

    @SpyBean
    private TransactionService transactionService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    private Transaction mockedTransaction1;

    private Transaction mockedTransaction2;

    @BeforeEach
    void beforeEach() {
        Transaction mockedTransaction1 = Transaction.builder()
                .date(LocalDate.now().minusMonths(1))
                .description("Transaction 1")
                .amount(randomAmount()).build();

        Transaction mockedTransaction2 = Transaction.builder()
                .date(LocalDate.now().minusMonths(2))
                .description("Transaction 2")
                .amount(randomAmount()).build();

        this.mockedTransaction1 = transactionRepository.save(mockedTransaction1);
        this.mockedTransaction2 = transactionRepository.save(mockedTransaction2);
    }

    @AfterEach
    void afterEach() {
        transactionRepository.deleteAll();
    }

    @Test
    void filterAndPage_Successfully() {
        SearchCriteria<Transaction> searchCriteria = new TransactionSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> pageResult = transactionService.filterAndPage(searchCriteria, pageable);

        boolean containsTransaction1 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction1.getId()));

        boolean containsTransaction2 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction2.getId()));

        assertTrue(containsTransaction1);
        assertTrue(containsTransaction2);
        assertEquals( 2, pageResult.getTotalElements());
    }

    @Test
    void filterAndPage_sortByDate_Successfully() {
        SearchCriteria<Transaction> searchCriteria = new TransactionSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").ascending());

        Page<Transaction> pageResult = transactionService.filterAndPage(searchCriteria, pageable);

        boolean containsTransaction1 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction1.getId()));

        assertEquals(pageResult.stream().findFirst().get().getId(), mockedTransaction2.getId());
        assertTrue(containsTransaction1);
        assertEquals( 2, pageResult.getTotalElements());
    }

    @Test
    void filterAndPage_filterByDate_Successfully() {
        LocalDate endDate = mockedTransaction2.getDate().plusDays(1);
        LocalDate startDate = mockedTransaction2.getDate().minusDays(10);

        TransactionSearchCriteria transactionSearchCriteria = new TransactionSearchCriteria();
        transactionSearchCriteria.setDateStart(startDate);
        transactionSearchCriteria.setDateEnd(endDate);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> pageResult = transactionService.filterAndPage(transactionSearchCriteria, pageable);

        boolean containsTransaction1 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction1.getId()));

        boolean containsTransaction2 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction2.getId()));

        assertFalse(containsTransaction1);
        assertTrue(containsTransaction2);
        assertEquals( 1, pageResult.getTotalElements());
    }

    @Test
    void filterAndPage_filterById_Successfully() {
        TransactionSearchCriteria transactionSearchCriteria = new TransactionSearchCriteria();
        transactionSearchCriteria.setId(mockedTransaction1.getId());

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> pageResult = transactionService.filterAndPage(transactionSearchCriteria, pageable);

        boolean containsTransaction1 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction1.getId()));

        boolean containsTransaction2 = pageResult.stream()
                .anyMatch(transaction -> transaction.getId().equals(mockedTransaction2.getId()));

        assertTrue(containsTransaction1);
        assertFalse(containsTransaction2);
        assertEquals( 1, pageResult.getTotalElements());
    }

    @Test
    void getTransactionByIdAndCurrency_Successfully() throws Exception {
        ExchangeRateDataDTO exchangeRateDataDto = getExchangeRateDataDto(mockedTransaction1.getDate());

        doReturn(exchangeRateDataDto).when(exchangeRateService)
                .retrieveExchangeForCurrency(FAKE_CURRENCY, mockedTransaction1.getDate());

        TransactionWithCurrencyConvertedDTO transactionByIdAndCurrency = transactionService
                .getTransactionByIdAndCurrency(mockedTransaction1.getId(), FAKE_CURRENCY);

        ExchangeRate exchangeRate = exchangeRateDataDto.getData().get(0).toEntity();
        BigDecimal expectedConvertedAmount = mockedTransaction1.getAmount()
                .multiply(exchangeRate.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(transactionByIdAndCurrency.getConvertedAmount(), expectedConvertedAmount);
    }

    @Test
    void givenEmptyExchangeData_whenGetTransactionByIdAndCurrency_thenExchangeRateClientException() {
        doReturn(new ExchangeRateDataDTO(List.of()))
                .when(exchangeRateService).retrieveExchangeForCurrency(FAKE_CURRENCY, mockedTransaction1.getDate());

        assertThatExceptionOfType(ExchangeRateClientException.class)
                .isThrownBy(() ->
                        transactionService.getTransactionByIdAndCurrency(mockedTransaction1.getId(), FAKE_CURRENCY))
                .withMessage("The purchase cannot be converted to the target currency.");
    }

    @Test
    void givenNonExistingTransaction_whenGetTransactionByIdAndCurrency_thenResourceNotFoundException() {
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() ->
                        transactionService.getTransactionByIdAndCurrency(UUID.randomUUID(), FAKE_CURRENCY))
                .withMessage("Transaction not found");
    }
}
