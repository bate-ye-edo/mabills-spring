package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.ExpenseCategoryAlreadyExistsException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ExpenseCategoryPersistence {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExpenseCategoryPersistence(ExpenseCategoryRepository expenseCategoryRepository, UserRepository userRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.userRepository = userRepository;
    }

    public List<ExpenseCategoryEntity> findExpenseCategoryByUserName(String username) {
        return expenseCategoryRepository.findByUser_Username(username);
    }

    public ExpenseCategoryEntity createExpenseCategory(String username, ExpenseCategory expenseCategory) {
        assertExpenseCategoryNotExistsForUser(username, expenseCategory.getName());
        return Try.of(()-> userRepository.findByUsername(username))
                .map(userEntity -> new ExpenseCategoryEntity(userEntity, expenseCategory))
                .map(expenseCategoryRepository::save)
                .getOrElseThrow(() -> new UserNotFoundException(username));
    }

    private void assertExpenseCategoryNotExistsForUser(String username, String expenseCategoryName) {
        if(expenseCategoryRepository.findByUser_UsernameAndName(username, expenseCategoryName) != null) {
            throw new ExpenseCategoryAlreadyExistsException(expenseCategoryName);
        }
    }

    public ExpenseCategoryEntity updateExpenseCategoryName(String username, UUID uuid, String name) throws UserNotFoundException {
        return Try.of(()->expenseCategoryRepository.findByUser_UsernameAndUuid(username, uuid))
                .map(expenseCategoryEntity -> {
                    expenseCategoryEntity.setName(name);
                    return expenseCategoryRepository.save(expenseCategoryEntity);
                })
                .getOrElseThrow(()->new ExpenseCategoryNotFoundException(uuid));
    }

    public void deleteExpenseCategory(String username, UUID uuid) {
        Try.of(()->expenseCategoryRepository.findByUser_UsernameAndUuid(username, uuid))
                .andThen(expenseCategoryRepository::delete)
                .onFailure(ex -> {
                    throw new ExpenseCategoryNotFoundException(uuid);
                });
    }
}
