package es.upm.mabills.persistence.repositories;

import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.TypePredicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Repository
@Profile("test")
@SuppressWarnings({"unused", "squid:S6437"})
public class DatabaseSeeder {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseSeeder.class);
    private static final String DESCRIPTION = "description";
    private static final Timestamp TODAY = new Timestamp(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
    private static final int LAST_DAY_OF_MONTH = LocalDate.now().lengthOfMonth();
    private static final Random RANDOM = new Random();
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final Environment environment;
    private String encodedPassword = "$2a$10$KyShpWQl4pS7KybIIZLkZ.6Mo2YBkPFuXT82cEOguWW3lpSMHgSEe";
    private String encodedPasswordUserName = "encodedPasswordUser";

    @Autowired
    public DatabaseSeeder(UserRepository userRepository, ExpenseCategoryRepository expenseCategoryRepository,
                          CreditCardRepository creditCardRepository, BankAccountRepository bankAccountRepository,
                          ExpenseRepository expenseRepository, IncomeRepository incomeRepository, PasswordEncoder passwordEncoder,
                          Environment environment) {

        if(Arrays.asList(environment.getActiveProfiles()).contains("dev")){
            encodedPassword = passwordEncoder.encode("1");
            encodedPasswordUserName = "1";
        }
        this.environment = environment;
        LOGGER.warn("----- Initialize database seeding -----");
        this.userRepository = userRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.creditCardRepository = creditCardRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.deleteAll();
        this.seedDatabase();
        LOGGER.warn("----- End -----");
    }

    private void deleteAll(){
        LOGGER.warn("----- Remove data from database -----");
        this.incomeRepository.deleteAll();
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
                .username(this.encodedPasswordUserName)
                .mobile("666666666")
                .password(encodedPassword)
                .email("newEmail")
                .build();
        UserEntity otherUser = UserEntity.builder()
                .username("otherUser")
                .mobile("6666666616")
                .password(encodedPassword)
                .email("otherEmail")
                .build();
        UserEntity logOutUser = UserEntity.builder()
                .username("logOutUser")
                .mobile("6666666616")
                .password(encodedPassword)
                .email("logoutEmail")
                .build();
        UserEntity expenseCategoryUser = UserEntity.builder()
                .username("expenseCategoryUser")
                .mobile("123123453412")
                .password(encodedPassword)
                .email("expenseCategoryEmail")
                .build();
        UserEntity toUpdateExpenseCategoryUserEntity = UserEntity.builder()
                .username("toUpdateExpenseCategoryUser")
                .mobile("1231233412")
                .password(encodedPassword)
                .email("toUpdateExpenseCategoryEmail")
                .build();
        UserEntity onlyUser = UserEntity.builder()
                .username("onlyUser")
                .mobile("123123341276788")
                .password(encodedPassword)
                .email("onlyUserEmail")
                .build();
        UserEntity toUpdateUser = UserEntity.builder()
                .username("toUpdateUser")
                .mobile("321123312")
                .password(encodedPassword)
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
        BankAccountEntity otherBankAccountEntity = BankAccountEntity.builder()
                .iban("ES004120003120034333")
                .user(encodedPasswordUser)
                .build();
        BankAccountEntity otherUserBankAccountEntity = BankAccountEntity.builder()
                .iban("ES004120003120034013")
                .user(otherUser)
                .build();
        this.bankAccountRepository.saveAll(List.of(bankAccountEntity, otherUserBankAccountEntity, toDeleteBankAccountEntity, toDeleteBankAccountEntityWithCreditCard, toDeleteBankAccountEntityWithCreditCardAndExpense, otherBankAccountEntity));

        // Credit cards
        CreditCardEntity creditCardEntity = CreditCardEntity.builder()
                .creditCardNumber("004120003120034012")
                .user(encodedPasswordUser)
                .build();
        CreditCardEntity creditCardEntityWithIncomeResource = CreditCardEntity.builder()
                .creditCardNumber("004120003120034000")
                .user(encodedPasswordUser)
                .build();
        CreditCardEntity creditCardWithBankAccountEntity = CreditCardEntity.builder()
                .creditCardNumber("004120012352345632")
                .user(encodedPasswordUser)
                .bankAccount(bankAccountEntity)
                .build();
        CreditCardEntity creditCardWithOtherBankAccountEntity = CreditCardEntity.builder()
                .creditCardNumber("004120012352345630")
                .user(encodedPasswordUser)
                .bankAccount(otherBankAccountEntity)
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
        this.creditCardRepository.saveAll(List.of(creditCardEntity, toDeleteCreditCard, creditCardToDelete, creditCardWithBankAccountToDelete, creditCardWithBankAccountEntity,
                creditCardWithBankAccountToDeleteAndExpense, creditCardEntityWithIncomeResource, creditCardWithOtherBankAccountEntity));

        // Expenses
        ExpenseEntity expenseEntity = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(userNameUserExpense)
                .creditCard(creditCardEntity)
                .bankAccount(bankAccountEntity)
                .build();
        ExpenseEntity expenseEntityWithCreditCardAndBankAccountToDelete = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(toDeleteExpenseCategoryWithExpenseResource)
                .creditCard(creditCardWithBankAccountToDeleteAndExpense)
                .bankAccount(toDeleteBankAccountEntityWithCreditCardAndExpense)
                .build();
        ExpenseEntity expenseEntityWithExpenseCategoryToDelete = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(toDeleteExpenseCategoryWithExpense)
                .creditCard(creditCardWithBankAccountToDeleteAndExpense)
                .bankAccount(toDeleteBankAccountEntityWithCreditCardAndExpense)
                .build();
        ExpenseEntity toUpdateExpense = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(encodedPasswordUserExpenseCategory)
                .creditCard(creditCardEntity)
                .build();
        ExpenseEntity toUpdateExpenseWithDependencies = ExpenseEntity.builder()
                .amount(BigDecimal.TEN)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description(DESCRIPTION)
                .formOfPayment(FormOfPayment.BANK_TRANSFER.name())
                .expenseCategory(encodedPasswordUserExpenseCategory)
                .creditCard(creditCardEntity)
                .build();
        ExpenseEntity toDeleteExpense = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description("to_delete_expense")
                .build();
        ExpenseEntity toDeleteExpenseResource = ExpenseEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .expenseDate(TODAY)
                .description("to_delete_expense_resource")
                .build();
        this.expenseRepository.saveAll(List.of(expenseEntity, expenseEntityWithCreditCardAndBankAccountToDelete, expenseEntityWithExpenseCategoryToDelete, toUpdateExpense, toUpdateExpenseWithDependencies, toDeleteExpense, toDeleteExpenseResource));

        // Incomes
        IncomeEntity incomeEntity = IncomeEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description(DESCRIPTION)
                .build();
        IncomeEntity toUpdateIncome = IncomeEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description(DESCRIPTION)
                .creditCard(creditCardEntity)
                .build();
        IncomeEntity toUpdateIncomeResource = IncomeEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description(DESCRIPTION)
                .creditCard(creditCardEntityWithIncomeResource)
                .build();
        IncomeEntity toUpdateIncomeWithDependencies = IncomeEntity.builder()
                .amount(BigDecimal.TEN)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description(DESCRIPTION)
                .creditCard(creditCardEntity)
                .build();
        IncomeEntity toDeleteIncome = IncomeEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description("to_delete_income")
                .build();
        IncomeEntity toDeleteIncomeResource = IncomeEntity.builder()
                .amount(BigDecimal.ONE)
                .user(encodedPasswordUser)
                .incomeDate(TODAY)
                .description("to_delete_income_resource")
                .build();
        this.incomeRepository.saveAll(List.of(incomeEntity, toUpdateIncomeResource, toUpdateIncome, toUpdateIncome, toUpdateIncomeWithDependencies, toDeleteIncome, toDeleteIncomeResource));

        if(Arrays.asList(environment.getActiveProfiles()).contains("dev")){
            seedDevDatabase(encodedPasswordUser);
        }

        LOGGER.warn("----- End seeding database -----");
    }

    private void seedDevDatabase(UserEntity encodedPasswordUser) {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .excludeType(TypePredicates.named("UUID"))
                .excludeField(FieldPredicates.named("id"))
                .excludeField(FieldPredicates.named("uuid"));

        EasyRandom easyRandom = new EasyRandom(parameters);

        List<BankAccountEntity> bankAccountEntities = easyRandom.objects(BankAccountEntity.class, 10)
                .peek(bankAccountEntity -> bankAccountEntity.setUser(encodedPasswordUser))
                .toList();
        this.bankAccountRepository.saveAll(bankAccountEntities);

        List<CreditCardEntity> creditCardEntities = easyRandom.objects(CreditCardEntity.class, 20)
                .peek(creditCardEntity -> {
                    BankAccountEntity bankAccountEntity = bankAccountEntities.get(easyRandom.nextInt(0, bankAccountEntities.size()));
                    creditCardEntity.setUser(encodedPasswordUser);
                    creditCardEntity.setBankAccount(bankAccountEntity);
                })
                .toList();
        this.creditCardRepository.saveAll(creditCardEntities);

        List<ExpenseCategoryEntity> expenseCategoryEntities = easyRandom.objects(ExpenseCategoryEntity.class, 10)
                .peek(expenseCategoryEntity -> expenseCategoryEntity.setUser(encodedPasswordUser))
                .toList();
        this.expenseCategoryRepository.saveAll(expenseCategoryEntities);

        List<ExpenseEntity> expenseEntities = easyRandom.objects(ExpenseEntity.class, 100)
                .peek(expenseEntity -> {
                    CreditCardEntity creditCardEntity = creditCardEntities.get(easyRandom.nextInt(0, creditCardEntities.size()));
                    expenseEntity.setUser(encodedPasswordUser);
                    expenseEntity.setCreditCard(creditCardEntity);
                    expenseEntity.setExpenseCategory(expenseCategoryEntities.get(easyRandom.nextInt(0, expenseCategoryEntities.size())));
                    expenseEntity.setBankAccount(creditCardEntity.getBankAccount());
                    expenseEntity.setFormOfPayment(easyRandom.nextObject(FormOfPayment.class).name());
                    expenseEntity.setExpenseDate(getRandomDayOfMonth());
                })
                .toList();
        this.expenseRepository.saveAll(expenseEntities);

        List<IncomeEntity> incomeEntities = easyRandom.objects(IncomeEntity.class, 100)
                .peek(incomeEntity -> {
                    CreditCardEntity creditCardEntity = creditCardEntities.get(easyRandom.nextInt(0, creditCardEntities.size()));
                    incomeEntity.setUser(encodedPasswordUser);
                    incomeEntity.setCreditCard(creditCardEntity);
                    incomeEntity.setBankAccount(creditCardEntity.getBankAccount());
                    incomeEntity.setIncomeDate(getRandomDayOfMonth());
                })
                .toList();
        this.incomeRepository.saveAll(incomeEntities);
    }

    private Timestamp getRandomDayOfMonth() {
        int day = RANDOM.nextInt(1, LAST_DAY_OF_MONTH);
        return new Timestamp(Date.from(LocalDate.now().withDayOfMonth(day).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
    }
}
