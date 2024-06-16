package es.upm.mabills.services.filters;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpensesFilterServiceTest {
    @Autowired
    private ExpensesFilterService expensesFilterService;

    @MockBean
    private FilterPersistence filterPersistence;

    @Test
    void testApplyFiltersSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any(UserPrincipal.class)))
            .thenReturn(List.of(ExpenseEntity.builder()
                            .amount(BigDecimal.ONE)
                            .description("description")
                            .build()));
        List<Expense> expenses = expensesFilterService.applyFilters(List.of(), new UserPrincipal());
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        assertEquals(BigDecimal.ONE, expenses.get(0).getAmount());
        assertEquals("description", expenses.get(0).getDescription());
    }

    @Test
    void testApplyFiltersEmpty() {
        when(filterPersistence.applyFilters(anyList(), any(), any(UserPrincipal.class)))
            .thenReturn(List.of());
        List<Expense> expenses = expensesFilterService.applyFilters(List.of(), new UserPrincipal());
        assertNotNull(expenses);
        assertTrue(expenses.isEmpty());
    }
}
