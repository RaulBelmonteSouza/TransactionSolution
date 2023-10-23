package com.raulbsouza.wex.TransactionSolution.searchcriteria;

import org.springframework.data.jpa.domain.Specification;

public interface SearchCriteria<T> {
    Specification<T> getSpecification();

    boolean isEmpty();
}
