package es.upm.mabills.services;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ExpenseMapper;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.services.dependency_validators.DependencyValidator;
import es.upm.mabills.services.exception_mappers.EntityNotFoundExceptionMapper;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseMapper expenseMapper;
    private final ExpensePersistence expensePersistence;
    private final DependencyValidator dependencyValidator;
    @Autowired
    public ExpenseService(ExpenseMapper expenseMapper, ExpensePersistence expensePersistence,
                          @Qualifier("expenseDependencyValidator") DependencyValidator dependencyValidator) {
        this.expenseMapper = expenseMapper;
        this.expensePersistence = expensePersistence;
        this.dependencyValidator = dependencyValidator;
    }

    public List<Expense> getUserExpenses(UserPrincipal userPrincipal) {
        return Try.of(() -> expensePersistence.findExpenseByUserId(userPrincipal)
                        .stream()
                        .map(expenseMapper::toExpense)
                        .toList())
                .getOrElseThrow(e -> new MaBillsServiceException());
    }

    public Expense createExpense(UserPrincipal userPrincipal, Expense expense) {
        dependencyValidator.assertDependencies(userPrincipal, expense);
        return Try.of(() -> expenseMapper.toExpense(expensePersistence.createExpense(userPrincipal, expense)))
                .getOrElseThrow(EntityNotFoundExceptionMapper::map);
    }

    @Transactional
    public Expense updateExpense(UserPrincipal userPrincipal, Expense expense) {
        dependencyValidator.assertDependencies(userPrincipal, expense);
        return Try.of(() -> expenseMapper.toExpense(expensePersistence.updateExpense(userPrincipal, expense)))
                .getOrElseThrow(EntityNotFoundExceptionMapper::map);
    }
}
