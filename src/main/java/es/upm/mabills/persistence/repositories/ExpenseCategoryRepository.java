package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {
    List<ExpenseCategoryEntity> findByUserId(int userId);
}
