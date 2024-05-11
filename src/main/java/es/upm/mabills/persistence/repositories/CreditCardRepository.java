package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, Integer>{
    List<CreditCardEntity> findByUser_Username(String username);
}
