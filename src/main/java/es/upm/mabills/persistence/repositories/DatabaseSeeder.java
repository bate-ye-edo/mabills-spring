package es.upm.mabills.persistence.repositories;

import es.upm.mabills.persistence.entities.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class DatabaseSeeder {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseSeeder.class);
    private final UserRepository userRepository;
    @Autowired
    public DatabaseSeeder(UserRepository userRepository) {
        LOGGER.warn("----- Initialize database seeding -----");
        LogManager.getLogger();
        this.userRepository = userRepository;
        this.deleteAll();
        this.seedDatabase();
        LOGGER.warn("----- End -----");
    }
    private void deleteAll(){
        LOGGER.warn("----- Delete database seeding -----");
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
        this.userRepository.save(userEntity);
    }
}