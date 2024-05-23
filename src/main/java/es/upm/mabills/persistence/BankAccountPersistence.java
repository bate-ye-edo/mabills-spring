package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.entity_decouplers.EntityDependentManager;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class BankAccountPersistence {
    private static final Logger LOGGER = LogManager.getLogger(BankAccountPersistence.class);

    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    private final EntityDependentManager entityDependentManager;

    @Autowired
    public BankAccountPersistence(BankAccountRepository bankAccountRepository, UserRepository userRepository,
                                  @Qualifier("bankAccountEntityRelationshipsManager") EntityDependentManager entityDependentManager) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.entityDependentManager = entityDependentManager;
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
                .andThen(this::deleteBankAccount)
                .onFailure(ex -> this.mapException(ex, uuid));
    }

    private void deleteBankAccount(BankAccountEntity bankAccountEntity) {
        entityDependentManager.decouple(bankAccountEntity.getUuid());
        bankAccountRepository.deleteById(bankAccountEntity.getUuid());
    }

    private void mapException(Throwable ex, String uuid) {
        if(ex instanceof NullPointerException) {
            throw new BankAccountNotFoundException(uuid);
        }
        LOGGER.error("Unexpected exception: ", ex);
        throw new MaBillsServiceException();
    }

    public BankAccountEntity findBankAccountByUserAndUuid(UserPrincipal userPrincipal, String uuid) {
        return bankAccountRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(uuid));
    }
}
