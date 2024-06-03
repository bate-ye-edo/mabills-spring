package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.IncomePersistence;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@UnitTestConfig
class IncomeServiceTest {

    @Autowired
    private IncomeService incomeService;

    @MockBean
    private IncomePersistence incomePersistence;

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
}
