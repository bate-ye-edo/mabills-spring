package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {
    List<ExpenseCategoryEntity> findByUser_Username(String username);
    ExpenseCategoryEntity findByUser_UsernameAndName(String username, String name);
    ExpenseCategoryEntity findByUser_UsernameAndUuid(String username, UUID uuid);
}
