package es.upm.mabills.mappers;

import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserMapper.class})
public interface ExpenseCategoryMapper {
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "user", source = "user", qualifiedByName = "toUser")
    ExpenseCategory toExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity);
}
