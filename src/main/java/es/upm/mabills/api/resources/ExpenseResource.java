package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.ExpenseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(ExpenseResource.EXPENSES)
public class ExpenseResource {
    public static final String EXPENSES = "/expenses";

    private final ExpenseService expenseService;

    public ExpenseResource(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getUserExpenses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.getUserExpenses(userPrincipal);
    }
}
