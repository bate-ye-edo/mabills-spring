package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
}
