package es.upm.mabills.services;

import es.upm.mabills.mappers.BankAccountMapper;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.persistence.BankAccountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountPersistence bankAccountPersistence;
    private final BankAccountMapper bankAccountMapper;
    @Autowired
    public BankAccountService(BankAccountPersistence bankAccountPersistence, BankAccountMapper bankAccountMapper) {
        this.bankAccountPersistence = bankAccountPersistence;
        this.bankAccountMapper = bankAccountMapper;
    }

    public List<BankAccount> findBankAccountsByUsername(String username) {
        return bankAccountPersistence.findBankAccountsByUsername(username)
                .stream()
                .map(bankAccountMapper::toBankAccount)
                .toList();
    }
}
