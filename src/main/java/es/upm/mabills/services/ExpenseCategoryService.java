package es.upm.mabills.services;

import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ExpenseCategoryMapper;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.ExpenseCategoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryPersistence expenseCategoryPersistence;
    private final ExpenseCategoryMapper expenseCategoryMapper;

    @Autowired
    public ExpenseCategoryService(ExpenseCategoryPersistence expenseCategoryPersistence, ExpenseCategoryMapper expenseCategoryMapper) {
        this.expenseCategoryPersistence = expenseCategoryPersistence;
        this.expenseCategoryMapper = expenseCategoryMapper;
    }

    public List<ExpenseCategory> getExpenseCategoriesByUserName(String username) {
        return expenseCategoryPersistence.findExpenseCategoryByUserName(username)
                .stream()
                .map(expenseCategoryMapper::toExpenseCategory)
                .toList();
    }

    public ExpenseCategory createExpenseCategory(String userName, ExpenseCategory expenseCategory) {
        return expenseCategoryMapper.toExpenseCategory(expenseCategoryPersistence.createExpenseCategory(userName, expenseCategory));
    }

    public ExpenseCategory updateExpenseCategoryName(String userName, UUID uuid, String name) {
        return expenseCategoryMapper.toExpenseCategory(expenseCategoryPersistence.updateExpenseCategoryName(userName, uuid, name));
    }

    @Transactional
    public void deleteExpenseCategory(String userName, UUID uuid) {
        try {
            expenseCategoryPersistence.deleteExpenseCategory(userName, uuid);
        } catch (ExpenseCategoryNotFoundException ex){
            throw ex;
        } catch (Exception e) {
            throw new MaBillsServiceException();
        }
    }
}
