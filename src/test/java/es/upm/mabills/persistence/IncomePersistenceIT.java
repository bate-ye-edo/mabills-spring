package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class IncomePersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String ONLY_USER = "onlyUser";
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
}
