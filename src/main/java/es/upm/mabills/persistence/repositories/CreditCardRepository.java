package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, UUID>{
    List<CreditCardEntity> findByUserId(int userId, Sort sort);
    CreditCardEntity findByUserIdAndCreditCardNumber(int userId, String creditCardNumber);
    CreditCardEntity findByUserIdAndUuid(int id, UUID uuid);
}
