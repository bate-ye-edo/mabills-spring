package es.upm.mabills.persistence.entity_decouplers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("creditCardEntityDependentManager")
public class CreditCardEntityDependentManager implements EntityDependentManager {
    private static final String CREDIT_CARD_ID = "credit_card_id";
    private final JdbcTemplate jdbcTemplate;
    private final EntityDependentQueryBuilder entityDependentQueryBuilder;

    @Autowired
    public CreditCardEntityDependentManager(JdbcTemplate jdbcTemplate, EntityDependentQueryBuilder entityDependentQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityDependentQueryBuilder = entityDependentQueryBuilder;
    }

    @Override
    public <T> void decouple(T id) {
        entityDependentQueryBuilder.buildRemoveFromDependentQuery(EntityDependentFactory.CREDIT_CARD, CREDIT_CARD_ID)
                .forEach(query -> jdbcTemplate.update(query, id));
    }
}
