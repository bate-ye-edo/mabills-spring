package es.upm.mabills.services;

import es.upm.mabills.exceptions.MaBillsUnexpectedException;
import es.upm.mabills.mappers.IncomeMapper;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.IncomePersistence;
import es.upm.mabills.services.dependency_validators.DependencyValidator;
import es.upm.mabills.services.exception_mappers.EntityNotFoundExceptionMapper;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IncomeService {
    private final IncomePersistence incomePersistence;
    private final IncomeMapper incomeMapper;
    private final DependencyValidator dependencyValidator;

    @Autowired
    public IncomeService(IncomePersistence incomePersistence, IncomeMapper incomeMapper,
                         @Qualifier("incomeDependencyValidator") DependencyValidator dependencyValidator) {
        this.incomePersistence = incomePersistence;
        this.incomeMapper = incomeMapper;
        this.dependencyValidator = dependencyValidator;
    }

    public List<Income> getUserIncomes(UserPrincipal userPrincipal) {
        return Try.of(() -> incomePersistence.findIncomesByUserId(userPrincipal)
                        .stream()
                        .map(incomeMapper::toIncome)
                        .toList())
                .getOrElseThrow(MaBillsUnexpectedException::new);
    }

    public Income createIncome(UserPrincipal userPrincipal, Income income) {
        dependencyValidator.assertDependencies(userPrincipal, income);
        return Try.of(() -> incomePersistence.createIncome(userPrincipal, income))
                .map(incomeMapper::toIncome)
                .getOrElseThrow(EntityNotFoundExceptionMapper::map);
    }

    public Income updateIncome(UserPrincipal userPrincipal, Income income) {
        dependencyValidator.assertDependencies(userPrincipal, income);
        return Try.of(() -> incomePersistence.updateIncome(userPrincipal, income))
                .map(incomeMapper::toIncome)
                .getOrElseThrow(EntityNotFoundExceptionMapper::map);
    }

    public void deleteIncome(UserPrincipal userPrincipal, String incomeUuid) {
        incomePersistence.deleteIncome(userPrincipal, incomeUuid);
    }
}
