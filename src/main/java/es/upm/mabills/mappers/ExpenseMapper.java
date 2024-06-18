package es.upm.mabills.mappers;

import es.upm.mabills.model.Expense;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { CreditCardMapper.class, ExpenseCategoryMapper.class })
public interface ExpenseMapper {
    @Mapping(target = "expenseDate", source = "expenseDate", dateFormat = "dd-MM-yyyy")
    Expense toExpense(ExpenseEntity expenseEntity);
}
