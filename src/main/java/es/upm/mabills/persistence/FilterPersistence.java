package es.upm.mabills.persistence;

import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterField;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
public class FilterPersistence {
    @PersistenceContext
    private EntityManager entityManager;

    public <T> List<T> applyFilters(List<Filter> filters, Class<T> clazz, UserPrincipal userPrincipal) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Predicate predicate = buildPredicates(filters, builder, root);
        predicate = builder.and(predicate, builder.equal(root.get("user").get("id"), userPrincipal.getId()));
        query.where(predicate);
        return entityManager.createQuery(query).getResultList();
    }

    private Predicate buildPredicates(List<Filter> filterList, CriteriaBuilder builder, Root<?> root) {
        Predicate predicate = builder.conjunction();
        for (Filter filter : filterList) {
            predicate = builder.and(predicate, getFilterPredicate(filter, builder, root));
        }
        return predicate;
    }

    private Path<?> addRelatedEntityNameToRoot(Root<?> root, FilterField field) {
        if(field.getEntityName() != null) {
            return root.get(field.getEntityName());
        }
        return root;
    }

    private Predicate getFilterPredicate(Filter filter, CriteriaBuilder builder, Root<?> root) {
        if(filter.getFilterField().equals(FilterField.EXPENSE_DATE) || filter.getFilterField().equals(FilterField.INCOME_DATE)) {
            return getDatePredicate(filter, builder, root);
        }
        return getDefaultPredicate(filter, builder, root);
    }

    private Predicate getDefaultPredicate(Filter filter, CriteriaBuilder builder, Root<?> root) {
        return filter.getFilterComparison()
                .getPredicate(
                        builder,
                        addRelatedEntityNameToRoot(root, filter.getFilterField()),
                        filter.getFilterField().getFieldName(),
                        filter.getFilterValue(),
                        filter.getSecondFilterValue()
                );
    }

    private Predicate getDatePredicate(Filter filter, CriteriaBuilder builder, Root<?> root) {
        return filter.getFilterComparison()
                .getPredicate(
                        builder,
                        addRelatedEntityNameToRoot(root, filter.getFilterField()),
                        filter.getFilterField().getFieldName(),
                        getTimestampFromDate(filter.getFilterValue()),
                        getTimestampFromDate(filter.getSecondFilterValue())
                );
    }

    private Timestamp getTimestampFromDate(String date) {
        if(Objects.isNull(date) || date.isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(date + " 00:00:00");
    }
}
