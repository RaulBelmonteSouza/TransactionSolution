package com.raulbsouza.wex.TransactionSolution.service;

import com.raulbsouza.wex.TransactionSolution.dto.TransactionDTO;
import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.repository.TransactionRepository;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction savedEntity = transactionRepository.save(transactionDTO.toEntity());
        transactionDTO = transactionDTO.toDTO(savedEntity);
        return transactionDTO;
    }

    public Page<Transaction> filterAndPage(SearchCriteria<Transaction> searchCriteria,
                                           Pageable pageable) {
        return transactionRepository.findAll(searchCriteria.getSpecification(), pageable);
    }

}
