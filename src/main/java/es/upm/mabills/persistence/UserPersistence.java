package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.DuplicatedEmailException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.UUID;


@Repository
public class UserPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPersistence.class);
    private final UserRepository userRepository;

    @Autowired
    public UserPersistence(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity registerUser(User user, String encodedPassword) throws UserAlreadyExistsException, DuplicatedEmailException {
        assertUserNotExists(user);
        return userRepository.save(new UserEntity(user, encodedPassword));
    }

    private void assertUserNotExists(User user) {
        if (findUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new DuplicatedEmailException(user.getEmail());
        }
    }

    public UserEntity updateUser(String username, User user) throws UserNotFoundException {
        return Try.of(()->userRepository.findByUsername(username))
                .map(userEntity ->{
                    userEntity.updateUserEntity(user);
                    LOGGER.info("User {} updated", userEntity.getUsername());
                    return userEntity;
                })
                .onFailure(NullPointerException.class, e -> {
                    throw new UserNotFoundException(username);
                })
                .andThenTry(userRepository::save)
                .getOrElseThrow(()->new DuplicatedEmailException(user.getEmail()));
    }

    public void assertUserHasBankAccount(UserEntity user, BankAccount bankAccount) {
        if(Objects.nonNull(bankAccount) && !isCreditCardBankAccountValid(user, bankAccount)) {
            throw new BankAccountNotFoundException(bankAccount.getIban());
        }
    }

    private boolean isCreditCardBankAccountValid(UserEntity user, BankAccount bankAccount) {
        return user
                .getBankAccounts()
                .stream()
                .anyMatch(bankAccountEntity -> bankAccountEntity.getUuid()
                        .compareTo(UUID.fromString(bankAccount.getUuid())) == 0);
    }

    public void assertUserHasCreditCard(UserEntity user, CreditCard creditCard) {
        if(Objects.nonNull(creditCard) && !isCreditCardValid(user, creditCard)) {
            throw new CreditCardNotFoundException(creditCard.getCreditCardNumber());
        }
    }

    private boolean isCreditCardValid(UserEntity user, CreditCard creditCard) {
        return user.getCreditCards()
                .stream()
                .anyMatch(creditCardEntity -> creditCardEntity.getUuid()
                        .compareTo(UUID.fromString(creditCard.getUuid())) == 0);
    }

    public void assertUserHasExpenseCategory(UserEntity user, ExpenseCategory expenseCategory) {
        if(Objects.nonNull(expenseCategory) && !isExpenseCategoryValid(user, expenseCategory)) {
            throw new ExpenseCategoryNotFoundException(UUID.fromString(expenseCategory.getUuid()));
        }
    }

    private boolean isExpenseCategoryValid(UserEntity user, ExpenseCategory expenseCategory) {
        return user.getExpenseCategories()
                .stream()
                .anyMatch(expenseCategoryEntity -> expenseCategoryEntity.getUuid()
                        .compareTo(UUID.fromString(expenseCategory.getUuid())) == 0);
    }
}
