package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {
    List<ExpenseCategoryEntity> findByUserId(int userId);
    ExpenseCategoryEntity findByUserIdAndName(int userId, String name);
    ExpenseCategoryEntity findByUserIdAndUuid(int userId, UUID uuid);
}
