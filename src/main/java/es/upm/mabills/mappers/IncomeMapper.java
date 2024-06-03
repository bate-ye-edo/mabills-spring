package es.upm.mabills.mappers;

import es.upm.mabills.model.Income;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {CreditCardMapper.class, BankAccountMapper.class})
public interface IncomeMapper {
    Income toIncome(IncomeEntity incomeEntity);
}
