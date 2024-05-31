package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.ExpenseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(ExpenseResource.EXPENSES)
public class ExpenseResource {
    public static final String EXPENSES = "/expenses";
    public static final String UUID = "/{uuid}";

    private final ExpenseService expenseService;

    public ExpenseResource(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getUserExpenses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.getUserExpenses(userPrincipal);
    }

    @PostMapping
    public Expense createExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated Expense expense) {
        return expenseService.createExpense(userPrincipal, expense);
    }

    @PutMapping
    public Expense updateExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated Expense expense) {
        return expenseService.updateExpense(userPrincipal, expense);
    }

    @DeleteMapping(UUID)
    public void deleteExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("uuid") String uuid) {
        expenseService.deleteExpense(userPrincipal, uuid);
    }
}
