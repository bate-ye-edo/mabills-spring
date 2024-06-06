package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
    List<ExpenseEntity> findByUserId(int userId, Sort sort);

    ExpenseEntity findByUserIdAndUuid(int userId, UUID uuid);

    @Modifying
    @Transactional
    @Query(value = "update expense set bank_account_id = null where bank_account_id = ?1", nativeQuery = true)
    void decoupleBankAccount(UUID bankAccountId);

    @Modifying
    @Transactional
    @Query(value = "update expense set credit_card_id = null where credit_card_id = ?1", nativeQuery = true)
    void decoupleCreditCard(UUID creditCardId);

    @Modifying
    @Transactional
    @Query(value = "update expense set expense_category_id = null where expense_category_id = ?1", nativeQuery = true)
    void decoupleExpenseCategory(Integer expenseCategoryId);

    @Query("select new es.upm.mabills.persistence.chart_data_dtos.DateChartData(e.expenseDate, sum(e.amount)) from ExpenseEntity e where e.user.id = ?1 " +
            "group by e.expenseDate order by e.expenseDate asc")
    List<DateChartData> findExpensesGroupByDate(int id);
}
