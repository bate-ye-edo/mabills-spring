package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class BankAccountPersistence {
    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    @Autowired
    public BankAccountPersistence(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public List<BankAccountEntity> findBankAccountsForUser(UserPrincipal userPrincipal) {
        return bankAccountRepository.findByUserId(userPrincipal.getId());
    }

    public BankAccountEntity createBankAccount(UserPrincipal userPrincipal, BankAccount bankAccount) {
        return this.bankAccountRepository.save(buildBankAccountEntity(userPrincipal, bankAccount));
    }

    private BankAccountEntity buildBankAccountEntity(UserPrincipal userPrincipal, BankAccount bankAccount) {
        UserEntity user = userRepository.getReferenceById(userPrincipal.getId());
        return BankAccountEntity.builder()
                .user(user)
                .iban(bankAccount.getIban())
                .build();
    }

    public void deleteBankAccount(UserPrincipal userPrincipal, String uuid) {
        Try.of(() -> bankAccountRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(uuid)))
                    .andThen(bankAccountRepository::delete)
                    .onFailure(ex -> {
                        throw new BankAccountNotFoundException();
                    });
    }
}
