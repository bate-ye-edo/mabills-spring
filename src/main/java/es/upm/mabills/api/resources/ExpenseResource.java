package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.api.dtos.FilterDto;
import es.upm.mabills.mappers.FilterMapper;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.ExpenseService;
import es.upm.mabills.services.filters.FilterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public static final String SEARCH = "/search";

    private final ExpenseService expenseService;
    private final FilterService<Expense> expensesFilterService;
    private final FilterMapper filterMapper;

    @Autowired
    public ExpenseResource(ExpenseService expenseService, @Qualifier("expensesFilterService") FilterService<Expense> expensesFilterService,
                           FilterMapper filterMapper) {
        this.expenseService = expenseService;
        this.expensesFilterService = expensesFilterService;
        this.filterMapper = filterMapper;
    }

    @GetMapping
    public List<Expense> getUserExpenses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.getUserExpenses(userPrincipal);
    }

    @PostMapping
    public Expense createExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid Expense expense) {
        return expenseService.createExpense(userPrincipal, expense);
    }

    @PutMapping
    public Expense updateExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid Expense expense) {
        return expenseService.updateExpense(userPrincipal, expense);
    }

    @DeleteMapping(UUID)
    public void deleteExpense(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("uuid") String uuid) {
        expenseService.deleteExpense(userPrincipal, uuid);
    }

    @PostMapping(SEARCH)
    public List<Expense> searchExpenses(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid List<FilterDto> filters) {
        return expensesFilterService.applyFilters(filters
                    .stream()
                    .map(filterMapper::toFilter)
                    .toList(),
                userPrincipal);
    }
}
