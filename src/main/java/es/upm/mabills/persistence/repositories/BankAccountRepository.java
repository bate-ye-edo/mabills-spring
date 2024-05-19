package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
    List<BankAccountEntity> findByUserId(int userId);
    BankAccountEntity findByUserIdAndUuid(int userId, UUID uuid);
}
