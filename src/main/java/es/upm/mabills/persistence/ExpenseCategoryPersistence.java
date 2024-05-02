package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.ExpenseCategoryAlreadyExistsException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.mappers.ExpenseCategoryMapper;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
            userPersistence.findUserIdByUsername(username))
        .stream()
        .map(expenseCategoryMapper::toExpenseCategory)
        .toList();
    }

    public ExpenseCategory createExpenseCategory(String userName, ExpenseCategory expenseCategory) {
        return Try.of(()->userPersistence.findUserIdByUsername(userName))
                .andThen(userId -> assertExpenseCategoryNotExistsForUser(userId, expenseCategory.getName()))
                .map(userId -> new ExpenseCategoryEntity(userId, expenseCategory.getName()))
                .map(expenseCategoryRepository::save)
                .map(expenseCategoryMapper::toExpenseCategory)
                .get();
    }

    private void assertExpenseCategoryNotExistsForUser(int userId, String expenseCategoryName) {
        if(expenseCategoryRepository.findByUserIdAndName(userId, expenseCategoryName) != null) {
            throw new ExpenseCategoryAlreadyExistsException(expenseCategoryName);
        }
    }

    public ExpenseCategory updateExpenseCategoryName(String userName, UUID uuid, String name) throws UserNotFoundException {
        int userId = userPersistence.findUserIdByUsername(userName);
        return Try.of(()->expenseCategoryRepository.findByUserIdAndUuid(userId, uuid))
                .map(expenseCategoryEntity -> {
                    expenseCategoryEntity.setName(name);
                    return expenseCategoryRepository.save(expenseCategoryEntity);
                })
                .map(expenseCategoryMapper::toExpenseCategory)
                .getOrElseThrow(()->new ExpenseCategoryNotFoundException(uuid));
    }
}
