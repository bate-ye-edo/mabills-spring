package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.IncomeNotFoundException;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.IncomePersistence;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.services.dependency_validators.DependencyValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTestConfig
class IncomeServiceTest {

    @Autowired
    private IncomeService incomeService;

    @MockBean
    private IncomePersistence incomePersistence;

    @MockBean(name = "incomeDependencyValidator")
    private DependencyValidator dependencyValidator;

    @Test
    void testGetUserIncomesEmpty() {
        when(incomePersistence.findIncomesByUserId(any(UserPrincipal.class))).thenReturn(List.of());
        UserPrincipal userPrincipal = new UserPrincipal();
        assertTrue(incomeService.getUserIncomes(userPrincipal).isEmpty());
    }

    @Test
    void testGetUserIncomesSuccess() {
        when(incomePersistence.findIncomesByUserId(any(UserPrincipal.class))).thenReturn(List.of(IncomeEntity.builder()
                .amount(BigDecimal.TEN)
                .build()));
        UserPrincipal userPrincipal = new UserPrincipal();
        List<Income> incomes = incomeService.getUserIncomes(userPrincipal);
        assertFalse(incomes.isEmpty());
        assertEquals(0, incomes.get(0).getAmount().compareTo(BigDecimal.TEN));
    }

    @Test
    void testUpdateIncomeSuccess() {
        IncomeEntity incomeEntity = buildIncomeEntity();
        when(incomePersistence.updateIncome(any(), any())).thenReturn(incomeEntity);
        Income updatedIncome = incomeService.updateIncome(UserPrincipal.builder().build(), Income.builder().build());
        assertNotNull(updatedIncome);
        assertEquals(incomeEntity.getAmount(), updatedIncome.getAmount());
        assertEquals(incomeEntity.getDescription(), updatedIncome.getDescription());
        assertEquals(incomeEntity.getIncomeDate(), updatedIncome.getIncomeDate());
    }

    @Test
    void testUpdateIncomeFailureByBankAccountNotFoundByDependencyValidator() {
        doThrow(BankAccountNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Income income = Income.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(BankAccountNotFoundException.class, () -> incomeService.updateIncome(userPrincipal, income));
    }

    @Test
    void testUpdateIncomeFailureByCreditCardNotFoundByDependencyValidator() {
        doThrow(CreditCardNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Income income = Income.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(CreditCardNotFoundException.class, () -> incomeService.updateIncome(userPrincipal, income));
    }

    @Test
    void testDeleteIncomeSuccess() {
        doNothing().when(incomePersistence).deleteIncome(any(), any());
        incomeService.deleteIncome(UserPrincipal.builder().build(), "uuid");
        verify(incomePersistence).deleteIncome(any(), any());
    }

    @Test
    void testDeleteIncomeFailure() {
        doThrow(IncomeNotFoundException.class).when(incomePersistence).deleteIncome(any(), any());
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(IncomeNotFoundException.class, () -> incomeService.deleteIncome(userPrincipal, "uuid"));
    }

    private IncomeEntity buildIncomeEntity() {
        return IncomeEntity.builder()
                .incomeDate(Timestamp.valueOf(LocalDateTime.now().minusDays(5)))
                .amount(BigDecimal.TEN)
                .description("description")
                .build();
    }
}
