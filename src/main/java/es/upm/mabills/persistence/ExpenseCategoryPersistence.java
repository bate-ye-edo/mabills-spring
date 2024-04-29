package es.upm.mabills.persistence;

import es.upm.mabills.mappers.ExpenseCategoryMapper;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseCategoryPersistence {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserPersistence userPersistence;
    private final ExpenseCategoryMapper expenseCategoryMapper;

    @Autowired
    public ExpenseCategoryPersistence(ExpenseCategoryRepository expenseCategoryRepository, UserPersistence userPersistence,
                                      ExpenseCategoryMapper expenseCategoryMapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.userPersistence = userPersistence;
        this.expenseCategoryMapper = expenseCategoryMapper;
    }

    public List<ExpenseCategory> findExpenseCategoryByUserName(String username) {
        return expenseCategoryRepository.findByUserId(
            this.userPersistence.findUserIdByUsername(username))
        .stream()
        .map(expenseCategoryMapper::toExpenseCategory)
        .toList();
    }
}
