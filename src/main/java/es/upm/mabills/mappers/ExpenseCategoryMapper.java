package es.upm.mabills.mappers;

import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ExpenseCategoryMapper {
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "uuid", source = "uuid")
    ExpenseCategory toExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity);
}
