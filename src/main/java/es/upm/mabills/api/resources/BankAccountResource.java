package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(BankAccountResource.BANK_ACCOUNTS)
public class BankAccountResource {
    public static final String BANK_ACCOUNTS = "/bank-accounts";
    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountResource(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public List<BankAccount> getUserBankAccounts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return bankAccountService.findBankAccountsForUser(userPrincipal);
    }

    @PostMapping
    public BankAccount createBankAccount(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated BankAccount bankAccount) {
        return bankAccountService.createBankAccount(userPrincipal, bankAccount);
    }
}
