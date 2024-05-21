package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpenseServiceTest {
    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private ExpensePersistence expensePersistence;

    @Test
    void testFindExpenseByUserId() {
        when(expensePersistence.findExpenseByUserId(any())).thenReturn(List.of(ExpenseEntity.builder().build()));
        assertFalse(expensePersistence.findExpenseByUserId(UserPrincipal.builder().build()).isEmpty());
    }

    @Test
    void testFindExpenseByUserIdNotFound() {
        when(expensePersistence.findExpenseByUserId(any())).thenReturn(List.of());
        assertTrue(expensePersistence.findExpenseByUserId(UserPrincipal.builder().build()).isEmpty());
    }
}
