package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("test")
public class DatabaseSeeder {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseSeeder.class);
    private static final String ENCODED_PASSWORD = "$2a$10$KyShpWQl4pS7KybIIZLkZ.6Mo2YBkPFuXT82cEOguWW3lpSMHgSEe";
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public DatabaseSeeder(UserRepository userRepository, ExpenseCategoryRepository expenseCategoryRepository,
                          CreditCardRepository creditCardRepository) {
        LOGGER.warn("----- Initialize database seeding -----");
        LogManager.getLogger();
        this.userRepository = userRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.creditCardRepository = creditCardRepository;
        this.deleteAll();
        this.seedDatabase();
        LOGGER.warn("----- End -----");
    }

    private void deleteAll(){
        LOGGER.warn("----- Delete database seeding -----");
        this.creditCardRepository.deleteAll();
        this.expenseCategoryRepository.deleteAll();
        this.userRepository.deleteAll();
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
        this.userRepository.saveAll(List.of(userEntity, encodedPasswordUser, otherUser, logOutUser, expenseCategoryUser, toUpdateExpenseCategoryUserEntity, onlyUser));

        // Expenses categories
        ExpenseCategoryEntity userNameUserExpense = ExpenseCategoryEntity.builder()
                .name("userNameUserExpenseCategory")
                .user(userEntity)
                .build();
        ExpenseCategoryEntity expenseCategoryUserExpense = ExpenseCategoryEntity.builder()
                .name("expenseCategoryUserExpenseCategory")
                .user(expenseCategoryUser)
                .build();
        this.expenseCategoryRepository.saveAll(List.of(userNameUserExpense, expenseCategoryUserExpense));

        CreditCardEntity creditCardEntity = CreditCardEntity.builder()
                .creditCardNumber("004120003120034012")
                .user(encodedPasswordUser)
                .build();

        this.creditCardRepository.saveAll(List.of(creditCardEntity));
        LOGGER.warn("----- End seeding database -----");
    }
}
