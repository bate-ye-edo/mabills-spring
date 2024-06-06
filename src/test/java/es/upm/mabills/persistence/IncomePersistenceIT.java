package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.IncomeNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class IncomePersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String ONLY_USER = "onlyUser";
    private static final String TO_UPDATE_INCOME_CREDIT_CARD_NUMBER = "004120003120034012";
    private static final String ANOTHER_DESCRIPTION = "anotherDescription";
    private static final String RANDOM_UUID = UUID.randomUUID().toString();
    private static final String TO_DELETE_INCOME_DESCRIPTION = "to_delete_income";

    @Autowired
    private IncomePersistence incomePersistence;

    @Autowired
    private UserRepository userRepository;

    private UserEntity encodedUserEntity;
    private UserPrincipal encodedUserPrincipal;
    private UserPrincipal onlyUserPrincipal;
    @BeforeEach
    void setUp() {
        UserEntity onlyUserEntity = userRepository.findByUsername(ONLY_USER);
        encodedUserEntity = userRepository.findByUsername(ENCODED_PASSWORD_USER);
        encodedUserPrincipal = UserPrincipal.builder().id(encodedUserEntity.getId()).username(encodedUserEntity.getUsername()).build();
        onlyUserPrincipal = UserPrincipal.builder().id(onlyUserEntity.getId()).username(onlyUserEntity.getUsername()).build();
    }

    @Test
    void testFindByUserIdEmptyIncomeSuccess() {
        List<IncomeEntity> incomeEntities = incomePersistence.findIncomesByUserId(onlyUserPrincipal);
        assertTrue(incomeEntities.isEmpty());
    }

    @Test
    void testFindByUserIdIncomeSuccess() {
        List<IncomeEntity> incomeEntities = incomePersistence.findIncomesByUserId(encodedUserPrincipal);
        assertFalse(incomeEntities.isEmpty());
    }

    @Test
    void testCreateIncomeWithoutDependenciesSuccess() {
        Income income = buildIncomeWithoutDependencies();
        IncomeEntity createdIncomeEntity = incomePersistence.createIncome(encodedUserPrincipal, income);
        assertEquals(income.getAmount(), createdIncomeEntity.getAmount());
        assertEquals(income.getDescription(), createdIncomeEntity.getDescription());
        assertEquals(income.getIncomeDate(), createdIncomeEntity.getIncomeDate());
    }

    @Test
    @Transactional
    void testCreateIncomeWithDependenciesSuccess() {
        Income income = buildIncomeWithDependencies();
        IncomeEntity createdIncomeEntity = incomePersistence.createIncome(encodedUserPrincipal, income);
        assertEquals(income.getAmount(), createdIncomeEntity.getAmount());
        assertEquals(income.getDescription(), createdIncomeEntity.getDescription());
        assertEquals(income.getIncomeDate(), createdIncomeEntity.getIncomeDate());
        assertEquals(income.getBankAccount().getIban(), createdIncomeEntity.getBankAccount().getIban());
        assertEquals(income.getCreditCard().getCreditCardNumber(), createdIncomeEntity.getCreditCard().getCreditCardNumber());
    }

    @Test
    void testUpdateIncomeSuccess() {
        Income income = buildIncomeToUpdate();
        IncomeEntity updatedIncomeEntity = incomePersistence.updateIncome(encodedUserPrincipal, income);
        assertNotNull(updatedIncomeEntity);
        assertEquals(income.getAmount(), updatedIncomeEntity.getAmount());
        assertEquals(income.getIncomeDate(), updatedIncomeEntity.getIncomeDate());
        assertEquals(income.getDescription(), updatedIncomeEntity.getDescription());
    }

    @Test
    void testUpdateIncomeIncomeNotFound() {
        Income expense = Income.builder().build();
        assertThrows(NullPointerException.class, () -> incomePersistence.updateIncome(encodedUserPrincipal, expense));
    }

    @Test
    void testUpdateIncomeCreditCardNotFound() {
        Income expense = buildIncomeToUpdateNotFoundCreditCard();
        assertThrows(DataIntegrityViolationException.class, () -> incomePersistence.updateIncome(encodedUserPrincipal, expense));
    }

    @Test
    void testUpdateIncomeBankAccountNotFound() {
        Income expense = buildIncomeToUpdateNotFoundBankAccount();
        assertThrows(DataIntegrityViolationException.class, () -> incomePersistence.updateIncome(encodedUserPrincipal, expense));
    }

    @Test
    @Transactional
    void testUpdateIncomeWithAllDependenciesSuccess() {
        Income expense = buildIncomeToUpdateWithAllDependencies();
        IncomeEntity updatedIncomeEntity = incomePersistence.updateIncome(encodedUserPrincipal, expense);
        assertNotNull(updatedIncomeEntity);
        assertEquals(expense.getAmount(), updatedIncomeEntity.getAmount());
        assertEquals(expense.getIncomeDate(), updatedIncomeEntity.getIncomeDate());
        assertEquals(expense.getDescription(), updatedIncomeEntity.getDescription());
        assertNotNull(updatedIncomeEntity.getBankAccount());
        assertEquals(expense.getBankAccount().getUuid(), updatedIncomeEntity.getBankAccount().getUuid().toString());
        assertNotNull(updatedIncomeEntity.getCreditCard());
        assertEquals(expense.getCreditCard().getUuid(), updatedIncomeEntity.getCreditCard().getUuid().toString());
    }

    @Test
    void testDeleteIncomeSuccess() {
        IncomeEntity incomeEntity = incomePersistence.findIncomesByUserId(encodedUserPrincipal)
                .stream()
                .filter(incomeEntity1 -> incomeEntity1.getDescription().equals(TO_DELETE_INCOME_DESCRIPTION))
                .findFirst()
                .orElseThrow();
        incomePersistence.deleteIncome(encodedUserPrincipal, incomeEntity.getUuid().toString());
        assertTrue(incomePersistence.findIncomesByUserId(encodedUserPrincipal)
                .stream()
                .noneMatch(incomeEntity1 -> incomeEntity1.getDescription().equals(TO_DELETE_INCOME_DESCRIPTION)));
    }

    @Test
    void testDeleteIncomeIncomeNotFound() {
        assertThrows(IncomeNotFoundException.class, () -> incomePersistence.deleteIncome(encodedUserPrincipal, RANDOM_UUID));
    }

    @Test
    void testGetExpensesGroupByDateChartDataSuccess() {
        List<DateChartData> dateChartDataList = incomePersistence.getIncomesGroupByDateChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByDateChartDataUserNotFound() {
        assertTrue(incomePersistence.getIncomesGroupByDateChartData(onlyUserPrincipal).isEmpty());
    }

    private Income buildIncomeWithDependencies() {
        return Income.builder()
                .amount(BigDecimal.TEN)
                .description("description")
                .bankAccount(buildBankAccountEntityReference())
                .creditCard(buildCreditCardEntityReference())
                .build();
    }

    private Income buildIncomeWithoutDependencies() {
        return Income.builder()
                .amount(BigDecimal.TEN)
                .description("description")
                .incomeDate(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    private BankAccount buildBankAccountEntityReference() {
        return BankAccount.builder()
                .uuid(encodedUserEntity.getBankAccounts().get(0).getUuid().toString())
                .iban(encodedUserEntity.getBankAccounts().get(0).getIban())
                .build();
    }

    private CreditCard buildCreditCardEntityReference() {
        return CreditCard.builder()
                .uuid(encodedUserEntity.getCreditCards().get(0).getUuid().toString())
                .creditCardNumber(encodedUserEntity.getCreditCards().get(0).getCreditCardNumber())
                .build();
    }

    private Income buildIncomeToUpdate() {
        return Income.builder()
                .uuid(findIncomeToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .build();
    }

    private IncomeEntity findIncomeToUpdate() {
        return incomePersistence.findIncomesByUserId(encodedUserPrincipal)
                .stream()
                .filter(expenseEntity -> expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_INCOME_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private Income buildIncomeToUpdateWithAllDependencies() {
        return Income.builder()
                .uuid(findIncomeWithDependenciesToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .bankAccount(buildBankAccountEntityReference())
                .creditCard(buildCreditCardEntityReference())
                .build();
    }

    private IncomeEntity findIncomeWithDependenciesToUpdate() {
        return incomePersistence.findIncomesByUserId(encodedUserPrincipal)
                .stream()
                .filter(expenseEntity -> expenseEntity.getAmount().compareTo(BigDecimal.TEN) == 0
                        && expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_INCOME_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private Income buildIncomeToUpdateNotFoundBankAccount() {
        return Income.builder()
                .uuid(findIncomeToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .bankAccount(BankAccount.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Income buildIncomeToUpdateNotFoundCreditCard() {
        return Income.builder()
                .uuid(findIncomeToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .creditCard(CreditCard.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }
}
