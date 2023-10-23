package com.raulbsouza.wex.TransactionSolution.searchcriteria.specifications;

import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SpecificationUtils {
    public static <T, P> Specification<T> isEqual(SingularAttribute<T, P> attribute, P value) {
        if (value == null) return null;
        return (root, query, builder) -> builder.equal(root.get(attribute), value);
    }


    public static <T,P extends Comparable<P>> Specification<T> isBetweenForLocalDate
            (SingularAttribute<T, LocalDate> attribute, LocalDate start, LocalDate end) {
        if (start == null || end == null) return null;
        return ((root, query, builder) -> builder.between(root.get(attribute), start, end));
    }
}
