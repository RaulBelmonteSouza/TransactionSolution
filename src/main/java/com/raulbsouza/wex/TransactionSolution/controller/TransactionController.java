package com.raulbsouza.wex.TransactionSolution.controller;

import com.raulbsouza.wex.TransactionSolution.dto.TransactionDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionWithCurrencyConvertedDTO;
import com.raulbsouza.wex.TransactionSolution.exception.ExchangeRateClientException;
import com.raulbsouza.wex.TransactionSolution.exception.ResourceNotFoundException;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.TransactionSearchCriteria;
import com.raulbsouza.wex.TransactionSolution.service.TransactionService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public TransactionDTO saveTransaction(@RequestBody @Validated TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @GetMapping
    public Page<TransactionDTO> getAll(@PageableDefault Pageable pageable,
                                       @Validated TransactionSearchCriteria criteria) {
        return transactionService.filterAndPage(criteria, pageable)
                .map(transaction -> new TransactionDTO().toDTO(transaction));
    }

    @GetMapping("/{id}")
    public TransactionWithCurrencyConvertedDTO getByIdAndCurrency
            (@PathVariable UUID id, @PathParam("currency") String currency)
            throws ResourceNotFoundException, ExchangeRateClientException {

        return transactionService.getTransactionByIdAndCurrency(id, currency);
    }

}
