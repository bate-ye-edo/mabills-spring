package es.upm.mabills.persistence.entity_dependent_managers;

import es.upm.mabills.persistence.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("creditCardEntityDependentManager")
public class CreditCardEntityDependentManager implements EntityDependentManager {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public CreditCardEntityDependentManager(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public <T> void decouple(T id) {
        expenseRepository.decoupleCreditCard((UUID) id);
    }
}
