package es.upm.mabills.model.filters;

import es.upm.mabills.exceptions.InvalidRequestException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@SuppressWarnings("unchecked")
public enum FilterComparisons {
    EQUAL((builder, root, fieldName, values) -> builder.equal(root.get(fieldName), values[0])),
    NOT_EQUAL((builder, root, fieldName, values) -> builder.notEqual(root.get(fieldName), values[0])),
    GREATER_THAN((builder, root, fieldName, values) -> builder.greaterThan(root.get(fieldName), (Comparable) values[0])),
    GREATER_THAN_OR_EQUAL((builder, root, fieldName, values) -> builder.greaterThanOrEqualTo(root.get(fieldName), (Comparable) values[0])),
    LESS_THAN((builder, root, fieldName, values) -> builder.lessThan(root.get(fieldName), (Comparable) values[0])),
    LESS_THAN_OR_EQUAL((builder, root, fieldName, values) -> builder.lessThanOrEqualTo(root.get(fieldName), (Comparable) values[0])),
    BETWEEN((builder, root, fieldName, values) -> builder.between(root.get(fieldName), (Comparable) values[0], (Comparable) values[1])),
    CONTAINS((builder, root, fieldName, values) -> builder.like(root.get(fieldName), "%"+ values[0] + "%")),;

    private final PredicateFilterFunction predicateFilterFunction;

    FilterComparisons(PredicateFilterFunction predicateFilterFunction) {
        this.predicateFilterFunction = predicateFilterFunction;
    }

    public Predicate getPredicate(CriteriaBuilder builder, Path<?> path, String fieldName, Object... values) {
        return predicateFilterFunction.getPredicate(builder, path, fieldName, values);
    }

    public static FilterComparisons fromString(String value) {
        for (FilterComparisons type : FilterComparisons.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid filter comparison type");
    }
}
