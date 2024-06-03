package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, UUID> {
    List<IncomeEntity> findByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value="update income set bank_account_id = null where bank_account_id = ?1", nativeQuery = true)
    void decoupleBankAccount(UUID bankAccountId);

    @Modifying
    @Transactional
    @Query(value="update income set credit_card_id = null where credit_card_id = ?1", nativeQuery = true)
    void decoupleCreditCard(UUID creditCardId);
}
