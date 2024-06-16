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

import java.util.List;

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
            predicate = builder.and(predicate, filter.getFilterComparison().getPredicate(
                    builder,
                    addRelatedEntityNameToRoot(root, filter.getFilterField()),
                    filter.getFilterField().getFieldName(),
                    filter.getFilterValue(),
                    filter.getSecondFilterValue()
            ));
        }
        return predicate;
    }

    private Path<?> addRelatedEntityNameToRoot(Root<?> root, FilterField field) {
        if(field.getEntityName() != null) {
            return root.get(field.getEntityName());
        }
        return root;
    }
}
