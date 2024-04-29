package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.services.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(ExpenseCategoryResource.EXPENSE_CATEGORIES)
public class ExpenseCategoryResource {
    public static final String EXPENSE_CATEGORIES = "/expense-categories";

    private final ExpenseCategoryService expenseCategoryService;

    @Autowired
    public ExpenseCategoryResource(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @GetMapping
    public List<ExpenseCategory> getExpenseCategories(@AuthenticationPrincipal String userName) {
        return this.expenseCategoryService.getExpenseCategoriesByUserName(userName);
    }
}
