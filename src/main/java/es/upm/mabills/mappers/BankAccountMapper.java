package es.upm.mabills.mappers;

import es.upm.mabills.model.BankAccount;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import org.mapstruct.Mapper;


@Mapper
public interface BankAccountMapper {
    BankAccount toBankAccount(BankAccountEntity bankAccountEntity);
}
