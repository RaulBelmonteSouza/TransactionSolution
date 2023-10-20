package com.raulbsouza.wex.TransactionSolution.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private UUID id;

    @Size(max = 50, message = "${description.not-valid}")
    private String description;

    private BigInteger amount;

    @Column(name = "transaction_date")
    @NotNull(message = "${date.null}")
    private LocalDate date;
}
