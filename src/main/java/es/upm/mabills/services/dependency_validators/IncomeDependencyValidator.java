package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.persistence.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static es.upm.mabills.services.dependency_validators.CommonValidations.assertCreditCardBankAccountAreRelated;
import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasBankAccount;
import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasCreditCard;

@Component
@Qualifier("incomeDependencyValidator")
public class IncomeDependencyValidator implements DependencyValidator {
    private final UserPersistence userPersistence;

    @Autowired
    public IncomeDependencyValidator(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public <T> void assertDependencies(UserPrincipal userPrincipal, T model) {
        Income income = (Income) model;
        UserEntity user = userPersistence.findUserByUsername(userPrincipal.getUsername());
        assertUserHasBankAccount(user, income.getBankAccount());
        assertUserHasCreditCard(user, income.getCreditCard());
        assertCreditCardBankAccountAreRelated(user, income.getCreditCard(), income.getBankAccount());
    }
}
