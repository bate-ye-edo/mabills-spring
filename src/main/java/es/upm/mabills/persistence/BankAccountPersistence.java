package es.upm.mabills.persistence;

import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
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

    public List<BankAccountEntity> findBankAccountsForUser(UserPrincipal userPrincipal) {
        return bankAccountRepository.findByUserId(userPrincipal.getId());
    }
}
