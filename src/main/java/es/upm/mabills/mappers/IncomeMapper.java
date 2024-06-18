package es.upm.mabills.mappers;

import es.upm.mabills.model.Income;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {CreditCardMapper.class, BankAccountMapper.class})
public interface IncomeMapper {
    @Mapping(target = "incomeDate", source = "incomeDate", dateFormat = "dd-MM-yyyy")
    Income toIncome(IncomeEntity incomeEntity);
}
