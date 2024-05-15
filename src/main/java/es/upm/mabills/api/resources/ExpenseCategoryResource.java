package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.api.dtos.UpdateExpenseCategoryDto;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.ExpenseCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Rest
@RequestMapping(ExpenseCategoryResource.EXPENSE_CATEGORIES)
public class ExpenseCategoryResource {
    public static final String EXPENSE_CATEGORIES = "/expense-categories";
    public static final String UUID = "/{uuid}";
    public static final String NAME = "/name";
    private final ExpenseCategoryService expenseCategoryService;

    @Autowired
    public ExpenseCategoryResource(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @GetMapping
    public List<ExpenseCategory> getExpenseCategories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return this.expenseCategoryService.getExpenseCategoriesByUserName(userPrincipal.getUsername());
    }

    @PostMapping
    public ExpenseCategory createExpenseCategory(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid ExpenseCategory expenseCategory) {
        return this.expenseCategoryService.createExpenseCategory(userPrincipal.getUsername(), expenseCategory);
    }

    @PutMapping(UUID + NAME)
    public ExpenseCategory updateExpenseCategory(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable("uuid") @NotNull UUID uuid,
                                                 @RequestBody @Valid UpdateExpenseCategoryDto dto) {
        return this.expenseCategoryService.updateExpenseCategoryName(userPrincipal.getUsername(), uuid, dto.name());
    }

    @DeleteMapping(UUID)
    public void deleteExpenseCategory(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("uuid") @NotNull UUID uuid) {
        this.expenseCategoryService.deleteExpenseCategory(userPrincipal.getUsername(), uuid);
    }
}
