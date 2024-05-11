package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreditCardPersistence {
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public CreditCardPersistence(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public List<CreditCardEntity> findCreditCardsByUserName(String username) {
        return Try.of(()->creditCardRepository.findByUser_Username(username))
                .getOrElseThrow(MaBillsServiceException::new);
    }
}
