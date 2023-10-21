package com.raulbsouza.wex.TransactionSolution.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {

    @Id
    @Column(name = "TRANSACTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Size(max = 50, message = "${description.not-valid}")
    private String description;

    private BigDecimal amount;

    @Column(name = "TRANSACTION_DATE")
    @NotNull(message = "${date.null}")
    private LocalDate date;
}
