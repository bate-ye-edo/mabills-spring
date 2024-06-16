package es.upm.mabills.services.filters;

import es.upm.mabills.TestConfig;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.IncomeEntity;
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

@TestConfig
class IncomesFilterServiceTest {
    @Autowired
    private IncomesFilterService expensesFilterService;

    @MockBean
    private FilterPersistence filterPersistence;

    @Test
    void testApplyFiltersSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any(UserPrincipal.class)))
                .thenReturn(List.of(IncomeEntity.builder()
                        .amount(BigDecimal.ONE)
                        .description("description")
                        .build()));
        List<Income> incomes = expensesFilterService.applyFilters(List.of(), new UserPrincipal());
        assertNotNull(incomes);
        assertFalse(incomes.isEmpty());
        assertEquals(1, incomes.size());
        assertEquals(BigDecimal.ONE, incomes.get(0).getAmount());
        assertEquals("description", incomes.get(0).getDescription());
    }

    @Test
    void testApplyFiltersEmpty() {
        when(filterPersistence.applyFilters(anyList(), any(), any(UserPrincipal.class)))
                .thenReturn(List.of());
        List<Income> incomes = expensesFilterService.applyFilters(List.of(), new UserPrincipal());
        assertNotNull(incomes);
        assertTrue(incomes.isEmpty());
    }
}
