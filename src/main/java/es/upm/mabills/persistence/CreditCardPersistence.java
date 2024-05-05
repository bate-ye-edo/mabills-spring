package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.CreditCardMapper;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreditCardPersistence {
    private final CreditCardRepository creditCardRepository;
    private final UserPersistence userPersistence;
    private final CreditCardMapper creditCardMapper;

    @Autowired
    public CreditCardPersistence(CreditCardRepository creditCardRepository, UserPersistence userPersistence,
                                 CreditCardMapper creditCardMapper) {
        this.creditCardRepository = creditCardRepository;
        this.userPersistence = userPersistence;
        this.creditCardMapper = creditCardMapper;
    }

    public List<CreditCard> findCreditCardsByUserName(String username) {
        int userId = userPersistence.findUserIdByUsername(username);
        return Try.of(()->creditCardRepository.findByUserId(userId))
                .map(creditCardEntities -> creditCardEntities.stream()
                        .map(creditCardMapper::toCreditCard)
                        .toList())
                .getOrElseThrow(MaBillsServiceException::new);
    }
}
