package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.persistence.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasBankAccount;

@Component
@Qualifier("creditCardDependencyValidator")
public class CreditCardDependencyValidator implements DependencyValidator {
    private final UserPersistence userPersistence;
    @Autowired
    public CreditCardDependencyValidator(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public <T> void assertDependencies(UserPrincipal userPrincipal, T model) {
        CreditCard creditCard = (CreditCard) model;
        UserEntity user = userPersistence.findUserByUsername(userPrincipal.getUsername());
        assertUserHasBankAccount(user, creditCard.getBankAccount());
    }
}
