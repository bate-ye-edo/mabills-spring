package es.upm.mabills.persistence.repositories;

import es.upm.mabills.model.ChartData;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, UUID> {
    List<IncomeEntity> findByUserId(int userId, Sort sort);

    IncomeEntity findByUserIdAndUuid(int userId, UUID uuid);

    @Modifying
    @Transactional
    @Query(value="update income set bank_account_id = null where bank_account_id = ?1", nativeQuery = true)
    void decoupleBankAccount(UUID bankAccountId);

    @Modifying
    @Transactional
    @Query(value="update income set credit_card_id = null where credit_card_id = ?1", nativeQuery = true)
    void decoupleCreditCard(UUID creditCardId);

    @Query("select new es.upm.mabills.persistence.chart_data_dtos.DateChartData(i.incomeDate, sum(i.amount)) from IncomeEntity i where i.user.id = ?1 " +
            "group by i.incomeDate order by i.incomeDate asc")
    List<DateChartData> findIncomesGroupByDate(int id);


    @Query("select new es.upm.mabills.model.ChartData(COALESCE(ib.iban, '') , sum(i.amount)) from IncomeEntity i" +
            " left join i.bankAccount ib " +
            " where i.user.id = ?1 " +
            " group by ib.iban ")
    List<ChartData> findIncomesGroupByBankAccount(int id);

    @Query("select new es.upm.mabills.model.ChartData(COALESCE(ic.creditCardNumber, '') , sum(i.amount)) from IncomeEntity i" +
            " left join i.creditCard ic " +
            " where i.user.id = ?1 " +
            " group by ic.creditCardNumber ")
    List<ChartData> findIncomesGroupByCreditCard(int id);
}
