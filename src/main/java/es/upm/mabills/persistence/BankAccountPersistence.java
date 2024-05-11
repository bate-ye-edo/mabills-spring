package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BankAccountPersistence {
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountPersistence(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<BankAccountEntity> findByIbanAndUserId(String username) {
        return Try.of(()->bankAccountRepository.findByUser_Username(username))
                .getOrElseThrow(MaBillsServiceException::new);
    }
}
