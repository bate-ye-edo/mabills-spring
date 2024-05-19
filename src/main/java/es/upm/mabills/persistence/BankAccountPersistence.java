package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.entity_decouplers.EntityRelationshipsManager;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class BankAccountPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountPersistence.class);

    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    private final EntityRelationshipsManager entityRelationshipsManager;

    @Autowired
    public BankAccountPersistence(BankAccountRepository bankAccountRepository, UserRepository userRepository,
                                  @Qualifier("bankAccountEntityRelationshipsManager") EntityRelationshipsManager entityRelationshipsManager) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.entityRelationshipsManager = entityRelationshipsManager;
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
        entityRelationshipsManager.decouple(bankAccountEntity.getUuid());
        bankAccountRepository.deleteById(bankAccountEntity.getUuid());
    }

    private void mapException(Throwable ex, String uuid) {
        if(ex instanceof NullPointerException) {
            throw new BankAccountNotFoundException(uuid);
        }
        LOGGER.error("Unexpected exception: ", ex);
        throw new MaBillsServiceException();
    }
}
