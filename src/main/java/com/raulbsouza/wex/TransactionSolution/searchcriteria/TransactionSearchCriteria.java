package com.raulbsouza.wex.TransactionSolution.searchcriteria;

import com.raulbsouza.wex.TransactionSolution.model.Transaction;
import com.raulbsouza.wex.TransactionSolution.searchcriteria.specifications.TransactionSpecifications;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransactionSearchCriteria implements SearchCriteria<Transaction> {

    private UUID id;
    private LocalDate dateStart;
    private LocalDate dateEnd;

    @Override
    public Specification<Transaction> getSpecification() {
        Specification<Transaction> specification = Specification
                .where(TransactionSpecifications.idEqual(id));

        if (dateStart != null && dateEnd != null) {
            specification = specification
                    .and(TransactionSpecifications.dateIsBetween(dateStart, dateEnd));
        }

        return specification;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
