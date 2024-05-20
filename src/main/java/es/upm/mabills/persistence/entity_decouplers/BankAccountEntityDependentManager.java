package es.upm.mabills.persistence.entity_decouplers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("bankAccountEntityRelationshipsManager")
public class BankAccountEntityDependentManager implements EntityDependentManager {
    private static final String BANK_ACCOUNT_ID = "bank_account_id";
    private final JdbcTemplate jdbcTemplate;
    private final EntityDependentQueryBuilder entityDependentQueryBuilder;

    @Autowired
    public BankAccountEntityDependentManager(JdbcTemplate jdbcTemplate, EntityDependentQueryBuilder entityDependentQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityDependentQueryBuilder = entityDependentQueryBuilder;
    }

    @Override
    public <T> void decouple(T id) {
        entityDependentQueryBuilder.buildRemoveFromDependentQuery(EntityDependentFactory.BANK_ACCOUNT, BANK_ACCOUNT_ID)
                .forEach(query -> jdbcTemplate.update(query, id));
    }
}
