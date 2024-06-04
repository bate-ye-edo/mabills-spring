package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.IncomeService;
import jakarta.validation.Valid;
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

@Rest
@RequestMapping(IncomeResource.INCOMES)
public class IncomeResource {
    public static final String INCOMES = "/incomes";
    public static final String UUID = "/{uuid}";

    private final IncomeService incomeService;

    @Autowired
    public IncomeResource(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public List<Income> getUserIncomes(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return incomeService.getUserIncomes(userPrincipal);
    }

    @PostMapping
    public Income createIncome(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid Income income) {
        return incomeService.createIncome(userPrincipal, income);
    }

    @PutMapping
    public Income updateIncome(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid Income income) {
        return incomeService.updateIncome(userPrincipal, income);
    }

    @DeleteMapping(UUID)
    public void deleteIncome(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("uuid") String uuid) {
        incomeService.deleteIncome(userPrincipal, uuid);
    }
}
