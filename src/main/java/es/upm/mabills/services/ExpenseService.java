package es.upm.mabills.services;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ExpenseMapper;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseMapper expenseMapper;
    private final ExpensePersistence expensePersistence;
    @Autowired
    public ExpenseService(ExpenseMapper expenseMapper, ExpensePersistence expensePersistence) {
        this.expenseMapper = expenseMapper;
        this.expensePersistence = expensePersistence;
    }

    public List<Expense> getUserExpenses(UserPrincipal userPrincipal) {
        return Try.of(() -> expensePersistence.findExpenseByUserId(userPrincipal)
                        .stream()
                        .map(expenseMapper::toExpense)
                        .toList())
                .getOrElseThrow(e -> new MaBillsServiceException());
    }
}
