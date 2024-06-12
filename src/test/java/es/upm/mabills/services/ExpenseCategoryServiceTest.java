package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.persistence.ExpenseCategoryPersistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@UnitTestConfig
class ExpenseCategoryServiceTest {
    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @MockBean
    private ExpenseCategoryPersistence expenseCategoryPersistence;

    @Test
    void testDeleteExpenseCategoryThrowsUnexpectedException() {
        doThrow(new RuntimeException()).when(expenseCategoryPersistence).deleteExpenseCategory(anyString(), any());
        assertThrows(RuntimeException.class, () -> expenseCategoryService.deleteExpenseCategory("username", null));
    }
}
