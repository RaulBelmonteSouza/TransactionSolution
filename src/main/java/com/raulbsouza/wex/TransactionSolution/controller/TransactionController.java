package com.raulbsouza.wex.TransactionSolution.controller;

import com.raulbsouza.wex.TransactionSolution.dto.TransactionDTO;
import com.raulbsouza.wex.TransactionSolution.dto.TransactionWithCurrencyConvertedDTO;
import com.raulbsouza.wex.TransactionSolution.exception.ExchangeRateClientException;
import com.raulbsouza.wex.TransactionSolution.exception.ResourceNotFoundException;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.TransactionSearchCriteria;
import com.raulbsouza.wex.TransactionSolution.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Create and save new Transaction")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO saveTransaction(@RequestBody @Validated TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @Operation(summary = "List Transactions by page, sort and filter.")
    @GetMapping
    public Page<TransactionDTO> getAll(@PageableDefault @ParameterObject Pageable pageable,
                                       @Validated @ParameterObject TransactionSearchCriteria criteria) {
        return transactionService.filterAndPage(criteria, pageable)
                .map(transaction -> new TransactionDTO().toDTO(transaction));
    }

    @Operation(summary = "Return a specified Transaction with amount converted based on the informed currency.")
    @GetMapping("/{id}")
    public TransactionWithCurrencyConvertedDTO getByIdAndCurrency
            (@PathVariable UUID id, @PathParam("currency")
            @Parameter(description = "Currency to convert, valid currency values can be found in the " +
                    "exchange-rates/country <a href=\"#/exchange-rate-controller/get\" target=\"blank\"> " +
                    "Exchange rates endpoint</a>") String currency)
            throws ResourceNotFoundException, ExchangeRateClientException {

        return transactionService.getTransactionByIdAndCurrency(id, currency);
    }

}
