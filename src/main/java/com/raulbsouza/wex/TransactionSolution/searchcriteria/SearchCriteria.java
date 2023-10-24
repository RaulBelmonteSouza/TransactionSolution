package com.raulbsouza.wex.TransactionSolution.searchcriteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.Specification;

public interface SearchCriteria<T> {

    @JsonIgnore
    Specification<T> getSpecification();

    @JsonIgnore
    boolean isEmpty();
}
