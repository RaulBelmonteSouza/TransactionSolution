package com.raulbsouza.wex.TransactionSolution.searchcriteria.specifications;

import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.model.Transaction_;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
public class TransactionSpecifications {

    public static Specification<Transaction> idEqual(UUID id) {
        return SpecificationUtils.isEqual(Transaction_.id, id);
    }

    public static Specification<Transaction> dateIsBetween(LocalDate begin, LocalDate end) {
        return SpecificationUtils.isBetweenForLocalDate(Transaction_.date, begin, end);
    }

}
