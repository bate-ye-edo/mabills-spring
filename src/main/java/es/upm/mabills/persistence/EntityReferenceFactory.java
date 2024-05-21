package es.upm.mabills.persistence;

import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@SuppressWarnings("unchecked")
public class EntityReferenceFactory {
    private final Map<Class<?>, JpaRepository<?, ?>> repositoriesMap;
    @Autowired
    public EntityReferenceFactory(UserRepository userRepository, BankAccountRepository bankAccountRepository,
                                  CreditCardRepository creditCardRepository, ExpenseRepository expenseRepository,
                                  ExpenseCategoryRepository expenseCategoryRepository) {
        repositoriesMap = Map.of(
                UserEntity.class, userRepository,
                BankAccountEntity.class, bankAccountRepository,
                CreditCardEntity.class, creditCardRepository,
                ExpenseEntity.class, expenseRepository,
                ExpenseCategoryEntity.class, expenseCategoryRepository);
    }

    public <T, I> T buildReference(Class<T> clazz, I id) {
        JpaRepository<T, I> repository = (JpaRepository<T, I>) repositoriesMap.get(clazz);
        return repository.getReferenceById(id);
    }
}
