package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, UUID>{
    List<CreditCardEntity> findByUserId(int userId, Sort sort);
    CreditCardEntity findByUserIdAndCreditCardNumber(int userId, String creditCardNumber);
    CreditCardEntity findByUserIdAndUuid(int id, UUID uuid);

    @Modifying
    @Transactional
    @Query(value = "update credit_card set bank_account_id = null where bank_account_id = ?1", nativeQuery = true)
    void decoupleBankAccount(UUID bankAccountId);
}
