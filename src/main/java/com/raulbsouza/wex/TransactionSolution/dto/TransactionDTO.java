package com.raulbsouza.wex.TransactionSolution.dto;

import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    private UUID id;

    @Size(max = 50, message = "{description.not-valid}")
    private String description;

    @NotNull
    @Positive(message = "{amount.not-valid}")
    private BigDecimal amount;

    @NotNull(message = "{date.null}")
    private LocalDate date;

    public Transaction toEntity() {
        return Transaction.builder()
                .id(id)
                .description(description)
                .amount(amount)
                .date(date)
                .build();
    }

    public TransactionDTO toDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .build();
    }
}
