package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.IncomeNotFoundException;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.IncomeRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class IncomePersistence {
    private final IncomeRepository incomeRepository;
    private final EntityReferenceFactory entityReferenceFactory;
    private final CreditCardReferenceBuilder creditCardReferenceBuilder;
    private final BankAccountReferenceBuilder bankAccountReferenceBuilder;

    @Autowired
    public IncomePersistence(IncomeRepository incomeRepository, EntityReferenceFactory entityReferenceFactory,
                             CreditCardReferenceBuilder creditCardReferenceBuilder, BankAccountReferenceBuilder bankAccountReferenceBuilder) {
        this.incomeRepository = incomeRepository;
        this.entityReferenceFactory = entityReferenceFactory;
        this.creditCardReferenceBuilder = creditCardReferenceBuilder;
        this.bankAccountReferenceBuilder = bankAccountReferenceBuilder;
    }

    public List<IncomeEntity> findIncomesByUserId(UserPrincipal userPrincipal) {
        return incomeRepository.findByUserId(userPrincipal.getId(), RepositorySort.BY_CREATION_DATE.value());
    }

    @Transactional
    public IncomeEntity createIncome(UserPrincipal userPrincipal, Income income) {
        return Try.of(() -> buildIncomeEntity(userPrincipal, income))
                .map(incomeRepository::save)
                .get();
    }

    @Transactional
    public IncomeEntity updateIncome(UserPrincipal userPrincipal, Income income) {
        return Try.of(() -> incomeRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(income.getUuid())))
                .map(incomeEntity -> {
                    incomeEntity.setAmount(income.getAmount());
                    incomeEntity.setIncomeDate(income.getIncomeDate());
                    incomeEntity.setDescription(income.getDescription());
                    incomeEntity.setCreditCard(creditCardReferenceBuilder.buildCreditCardEntityReference(income.getCreditCard()));
                    incomeEntity.setBankAccount(bankAccountReferenceBuilder.buildBankAccountEntityReference(income.getBankAccount(), income.getCreditCard(), userPrincipal));
                    return incomeRepository.save(incomeEntity);
                })
                .get();
    }

    private IncomeEntity buildIncomeEntity(UserPrincipal userPrincipal, Income income) {
        return IncomeEntity.builder()
                .user(entityReferenceFactory.buildReference(UserEntity.class, userPrincipal.getId()))
                .amount(income.getAmount())
                .description(income.getDescription())
                .incomeDate(income.getIncomeDate())
                .creditCard(creditCardReferenceBuilder.buildCreditCardEntityReference(income.getCreditCard()))
                .bankAccount(bankAccountReferenceBuilder.buildBankAccountEntityReference(income.getBankAccount(), income.getCreditCard(), userPrincipal))
                .build();
    }


    public void deleteIncome(UserPrincipal userPrincipal, String incomeUuid) {
        Try.of(() -> incomeRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(incomeUuid)))
                .andThen(incomeRepository::delete)
                .onFailure(ex -> {
                    throw new IncomeNotFoundException(incomeUuid);
                });
    }
}
