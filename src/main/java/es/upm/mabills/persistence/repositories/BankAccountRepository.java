package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Integer> {
    List<BankAccountEntity> findByUser_Username(String username);
}
