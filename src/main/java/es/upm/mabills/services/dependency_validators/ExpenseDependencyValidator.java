package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.persistence.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import static es.upm.mabills.services.dependency_validators.CommonValidations.assertCreditCardBankAccountAreRelated;
import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasBankAccount;
import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasCreditCard;
import static es.upm.mabills.services.dependency_validators.CommonValidations.assertUserHasExpenseCategory;

@Component
@Qualifier("expenseDependencyValidator")
public class ExpenseDependencyValidator implements DependencyValidator {
    private final UserPersistence userPersistence;

    @Autowired
    public ExpenseDependencyValidator(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public <T> void assertDependencies(UserPrincipal userPrincipal, T model) {
        Expense expense = (Expense) model;
        UserEntity user = userPersistence.findUserByUsername(userPrincipal.getUsername());
        assertUserHasBankAccount(user, expense.getBankAccount());
        assertUserHasCreditCard(user, expense.getCreditCard());
        assertUserHasExpenseCategory(user, expense.getExpenseCategory());
        assertCreditCardBankAccountAreRelated(user, expense.getCreditCard(), expense.getBankAccount());
    }

}
