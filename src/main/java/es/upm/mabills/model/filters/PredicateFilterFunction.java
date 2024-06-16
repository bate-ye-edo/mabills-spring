package es.upm.mabills.model.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;


@FunctionalInterface
public interface PredicateFilterFunction {
    Predicate getPredicate(CriteriaBuilder builder, Path<?> root, String fieldName, Object... values);
}
