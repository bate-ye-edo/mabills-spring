package es.upm.mabills.mappers;

import es.upm.mabills.model.Expense;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.mapstruct.Mapper;

@Mapper(uses = { CreditCardMapper.class, ExpenseCategoryMapper.class })
public interface ExpenseMapper {
    Expense toExpense(ExpenseEntity expenseEntity);
}
