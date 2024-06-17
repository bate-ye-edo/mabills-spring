package es.upm.mabills.services;

import es.upm.mabills.exceptions.BankAccountAlreadyExistsException;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.MaBillsUnexpectedException;
import es.upm.mabills.mappers.BankAccountMapper;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.BankAccountPersistence;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankAccountService {
    private static final Logger LOGGER = LogManager.getLogger(BankAccountService.class);
    private final BankAccountPersistence bankAccountPersistence;
    private final BankAccountMapper bankAccountMapper;

    @Autowired
    public BankAccountService(BankAccountPersistence bankAccountPersistence, BankAccountMapper bankAccountMapper) {
        this.bankAccountPersistence = bankAccountPersistence;
        this.bankAccountMapper = bankAccountMapper;
    }

    public List<BankAccount> findBankAccountsForUser(UserPrincipal userPrincipal) {
        return bankAccountPersistence.findBankAccountsForUser(userPrincipal)
                .stream()
                .map(bankAccountMapper::toBankAccount)
                .toList();
    }

    public BankAccount createBankAccount(UserPrincipal userPrincipal, BankAccount bankAccount) {
        return Try.of(() -> bankAccountMapper.toBankAccount(bankAccountPersistence.createBankAccount(userPrincipal, bankAccount)))
                .getOrElseThrow(() -> new BankAccountAlreadyExistsException(userPrincipal.getUsername(), bankAccount.getIban()));
    }

    @Transactional
    public void deleteBankAccount(UserPrincipal userPrincipal, String uuid) {
        try {
            bankAccountPersistence.deleteBankAccount(userPrincipal, uuid);
        } catch (BankAccountNotFoundException bEx) {
            LOGGER.error("Bank account {} for user {} not found", uuid, userPrincipal.getUsername());
            throw bEx;
        } catch (Exception e) {
            throw new MaBillsUnexpectedException();
        }
    }
}
