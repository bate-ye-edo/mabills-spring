package es.upm.mabills.persistence.repositories;

import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Profile("test")
public class DatabaseSeeder {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseSeeder.class);
    private static final String ENCODED_PASSWORD = "$2a$10$KyShpWQl4pS7KybIIZLkZ.6Mo2YBkPFuXT82cEOguWW3lpSMHgSEe";
    private static final String DESCRIPTION = "description";
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ExpenseRepository expenseRepository;
    @Autowired
    public DatabaseSeeder(UserRepository userRepository, ExpenseCategoryRepository expenseCategoryRepository,
                          CreditCardRepository creditCardRepository, BankAccountRepository bankAccountRepository,
                          ExpenseRepository expenseRepository) {
        LOGGER.warn("----- Initialize database seeding -----");
        this.userRepository = userRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.creditCardRepository = creditCardRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.deleteAll();
        this.seedDatabase();
        LOGGER.warn("----- End -----");
    }

    private void deleteAll(){
        LOGGER.warn("----- Remove data from database -----");
        this.expenseRepository.deleteAll();
        this.creditCardRepository.deleteAll();
        this.bankAccountRepository.deleteAll();
        this.expenseCategoryRepository.deleteAll();
        this.userRepository.deleteAll();
        LOGGER.warn("----- End removing data -----");
    }

    private void seedDatabase() {
        LOGGER.warn("----- Seeding database -----");
        UserEntity userEntity = UserEntity.builder()
                .username("username")
                .mobile("666666666")
                .password("password")
                .email("email")
                .build();
        UserEntity encodedPasswordUser = UserEntity.builder()
                .username("encodedPasswordUser")
                .mobile("666666666")
                .password(ENCODED_PASSWORD)
                .email("newEmail")
                .build();
        UserEntity otherUser = UserEntity.builder()
                .username("otherUser")
                .mobile("6666666616")
                .password(ENCODED_PASSWORD)
                .email("otherEmail")
                .build();
        UserEntity logOutUser = UserEntity.builder()
                .username("logOutUser")
                .mobile("6666666616")
                .password(ENCODED_PASSWORD)
                .email("logoutEmail")
                .build();
        UserEntity expenseCategoryUser = UserEntity.builder()
                .username("expenseCategoryUser")
                .mobile("123123453412")
                .password(ENCODED_PASSWORD)
                .email("expenseCategoryEmail")
                .build();
        UserEntity toUpdateExpenseCategoryUserEntity = UserEntity.builder()
                .username("toUpdateExpenseCategoryUser")
                .mobile("1231233412")
                .password(ENCODED_PASSWORD)
                .email("toUpdateExpenseCategoryEmail")
                .build();
        UserEntity onlyUser = UserEntity.builder()
                .username("onlyUser")
                .mobile("123123341276788")
                .password(ENCODED_PASSWORD)
                .email("onlyUserEmail")
                .build();
        UserEntity toUpdateUser = UserEntity.builder()
                .username("toUpdateUser")
                .mobile("321123312")
                .password(ENCODED_PASSWORD)
                .email("toUpdateUserEmail")
                .build();
        this.userRepository.saveAll(List.of(userEntity, encodedPasswordUser, otherUser, logOutUser, expenseCategoryUser, toUpdateExpenseCategoryUserEntity, onlyUser, toUpdateUser));

        // Expenses categories
        ExpenseCategoryEntity userNameUserExpense = ExpenseCategoryEntity.builder()
                .name("userNameUserExpenseCategory")
                .user(userEntity)
                .build();
        ExpenseCategoryEntity expenseCategoryUserExpense = ExpenseCategoryEntity.builder()
                .name("expenseCategoryUserExpenseCategory")
                .user(expenseCategoryUser)
                .build();
        ExpenseCategoryEntity encodedPasswordUserExpenseCategory = ExpenseCategoryEntity.builder()
                .name("encodedPasswordUserExpenseCategory")
                .user(encodedPasswordUser)
                .build();
        ExpenseCategoryEntity toDeleteExpenseCategoryWithExpense = ExpenseCategoryEntity.builder()
                .name("toDeleteExpenseCategoryWithExpense")
                .user(encodedPasswordUser)
                .build();
        ExpenseCategoryEntity toDeleteExpenseCategoryWithExpenseResource = ExpenseCategoryEntity.builder()
                .name("toDeleteExpenseCategoryWithExpenseResource")
                .user(encodedPasswordUser)
                .build();
        this.expenseCategoryRepository.saveAll(List.of(userNameUserExpense, expenseCategoryUserExpense, encodedPasswordUserExpenseCategory, toDeleteExpenseCategoryWithExpense, toDeleteExpenseCategoryWithExpenseResource));


        // Bank accounts
        BankAccountEntity bankAccountEntity = BankAccountEntity.builder()
                .iban("ES004120003120034012")
                .user(encodedPasswordUser)
                .build();
        BankAccountEntity toDeleteBankAccountEntity = BankAccountEntity.builder()
                .iban("to_delete_bank_account")
                .user(encodedPasswordUser)
                .build();
        BankAccountEntity toDeleteBankAccountEntityWithCreditCard = BankAccountEntity.builder()
                .iban("to_delete_bank_account_entity_with_credit_card")
                .user(encodedPasswordUser)
                .build();
        BankAccountEntity toDeleteBankAccountEntityWithCreditCardAndExpense = BankAccountEntity.builder()
                .iban("to_delete_bank_account_entity_with_credit_card_and_expense")
                .user(encodedPasswordUser)
                .build();
        BankAccountEntity otherUserBankAccountEntity = BankAccountEntity.builder()
                .iban("ES004120003120034013")
                .user(otherUser)
                .build();
        this.bankAccountRepository.saveAll(List.of(bankAccountEntity, otherUserBankAccountEntity, toDeleteBankAccountEntity, toDeleteBankAccountEntityWithCreditCard, toDeleteBankAccountEntityWithCreditCardAndExpense));

        // Credit cards
        CreditCardEntity creditCardEntity = CreditCardEntity.builder()
                .creditCardNumber("004120003120034012")
                .user(encodedPasswordUser)
                .build();
        CreditCardEntity creditCardWithBankAccountEntity = CreditCardEntity.builder()
                .creditCardNumber("004120012352345632")
                .user(encodedPasswordUser)
                .bankAccount(bankAccountEntity)
                .build();
        CreditCardEntity toDeleteCreditCard = CreditCardEntity.builder()
            .creditCardNumber("to_delete_credit_card_number")
            .user(encodedPasswordUser)
            .build();
        CreditCardEntity creditCardToDelete = CreditCardEntity.builder()
                .creditCardNumber("005130013120034012")
                .user(otherUser)
                .build();
        CreditCardEntity creditCardWithBankAccountToDelete = CreditCardEntity.builder()
                .bankAccount(toDeleteBankAccountEntityWithCreditCard)
                .user(encodedPasswordUser)
                .creditCardNumber("bank_account_will_be_deleted")
                .build();
        CreditCardEntity creditCardWithBankAccountToDeleteAndExpense = CreditCardEntity.builder()
                .bankAccount(toDeleteBankAccountEntityWithCreditCardAndExpense)
                .user(encodedPasswordUser)
                .creditCardNumber("bank_account_will_be_deleted_and_expense")
                .build();
        this.creditCardRepository.saveAll(List.of(creditCardEntity, toDeleteCreditCard, creditCardToDelete, creditCardWithBankAccountToDelete, creditCardWithBankAccountEntity, creditCardWithBankAccountToDeleteAndExpense));

        // Expenses
        ExpenseEntity expenseEntity = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(new Timestamp(System.currentTimeMillis()))
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(userNameUserExpense)
                .creditCard(creditCardEntity)
                .bankAccount(bankAccountEntity)
                .build();
        ExpenseEntity expenseEntityWithCreditCardAndBankAccountToDelete = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(new Timestamp(System.currentTimeMillis()))
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(toDeleteExpenseCategoryWithExpenseResource)
                .creditCard(creditCardWithBankAccountToDeleteAndExpense)
                .bankAccount(toDeleteBankAccountEntityWithCreditCardAndExpense)
                .build();
        ExpenseEntity expenseEntityWithExpenseCategoryToDelete = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(new Timestamp(System.currentTimeMillis()))
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(toDeleteExpenseCategoryWithExpense)
                .creditCard(creditCardWithBankAccountToDeleteAndExpense)
                .bankAccount(toDeleteBankAccountEntityWithCreditCardAndExpense)
                .build();
        ExpenseEntity toUpdateExpense = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(new Timestamp(System.currentTimeMillis()))
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(encodedPasswordUserExpenseCategory)
                .creditCard(creditCardEntity)
                .build();
        ExpenseEntity toUpdateExpenseWithDependencies = ExpenseEntity.builder()
                .amount(BigDecimal.TEN)
                .user(encodedPasswordUser)
                .expenseDate(new Timestamp(System.currentTimeMillis()))
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(encodedPasswordUserExpenseCategory)
                .creditCard(creditCardEntity)
                .build();
        this.expenseRepository.saveAll(List.of(expenseEntity, expenseEntityWithCreditCardAndBankAccountToDelete, expenseEntityWithExpenseCategoryToDelete, toUpdateExpense, toUpdateExpenseWithDependencies));
        LOGGER.warn("----- End seeding database -----");
    }
}
