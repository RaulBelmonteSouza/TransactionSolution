package com.raulbsouza.wex.TransactionSolution.service;

import com.raulbsouza.wex.TransactionSolution.client.ExchangeRateFields;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDTO;
import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionWithCurrencyConvertedDTO;
import com.raulbsouza.wex.TransactionSolution.exception.ExchangeRateClientException;
import com.raulbsouza.wex.TransactionSolution.exception.ResourceNotFoundException;
import com.raulbsouza.wex.TransactionSolution.model.ExchangeRate;
import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.repository.TransactionRepository;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        transactionDTO.setAmount(transactionDTO.getAmount().setScale(2, RoundingMode.HALF_UP));
        Transaction savedEntity = transactionRepository.save(transactionDTO.toEntity());
        transactionDTO = transactionDTO.toDTO(savedEntity);
        return transactionDTO;
    }

    public Page<Transaction> filterAndPage(SearchCriteria<Transaction> searchCriteria,
                                           Pageable pageable) {
        return transactionRepository.findAll(searchCriteria.getSpecification(), pageable);
    }

    public TransactionWithCurrencyConvertedDTO getTransactionByIdAndCurrency(UUID id, String currency)
            throws ResourceNotFoundException, ExchangeRateClientException {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        ExchangeRateDTO exchangeRateDTO = getExchangeRate(currency, transaction.getDate());
        ExchangeRate exchangeRate = exchangeRateDTO.toEntity();

        BigDecimal convertedAmount = transaction.getAmount()
                .multiply(exchangeRate.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);

        return TransactionWithCurrencyConvertedDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .transactionDate(transaction.getDate())
                .originalCurrency(ExchangeRateFields.DEFAULT_CURRENCY)
                .originalAmount(transaction.getAmount())
                .convertedAmount(convertedAmount)
                .conversionCurrency(currency)
                .exchangeRate(exchangeRate.getExchangeRate())
                .build();
    }

    private ExchangeRateDTO getExchangeRate(String currency, LocalDate date) throws ExchangeRateClientException {
        ExchangeRateDataDTO exchangeRateDataDTO = exchangeRateService
                .retrieveExchangeForCurrency(currency, date);

        if (exchangeRateDataDTO.getData().isEmpty())
            throw new ExchangeRateClientException("The purchase cannot be converted to the target currency.");

        return exchangeRateDataDTO.getData().get(0);
    }

}
