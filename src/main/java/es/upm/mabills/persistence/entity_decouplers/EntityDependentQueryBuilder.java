package es.upm.mabills.persistence.entity_decouplers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityDependentQueryBuilder {

    public List<String> buildRemoveFromDependentQuery(EntityDependentFactory entityDependentFactory, String fieldNameInDependent) {
        return entityDependentFactory.getEntityDependentList()
                .stream()
                .map(dependantEntityName -> buildUpdateToNullQuery(dependantEntityName, fieldNameInDependent))
                .toList();
    }

    private String buildUpdateToNullQuery(String dependantEntityName, String fieldNameInDependent) {
        return String.format("UPDATE %s SET %s = null WHERE %S = ?", dependantEntityName, fieldNameInDependent, fieldNameInDependent);
    }

}
