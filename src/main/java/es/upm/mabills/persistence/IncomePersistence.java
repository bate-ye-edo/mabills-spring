package es.upm.mabills.persistence;

import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.IncomeRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return incomeRepository.findByUserId(userPrincipal.getId());
    }

    @Transactional
    public IncomeEntity createIncome(UserPrincipal userPrincipal, Income income) {
        return Try.of(() -> buildIncomeEntity(userPrincipal, income))
                .map(incomeRepository::save)
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
}
