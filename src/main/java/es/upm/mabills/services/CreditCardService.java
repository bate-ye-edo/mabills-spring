package es.upm.mabills.services;

import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.CreditCardPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardService {
    private final CreditCardPersistence creditCardPersistence;

    @Autowired
    public CreditCardService(CreditCardPersistence creditCardPersistence) {
        this.creditCardPersistence = creditCardPersistence;
    }

    public List<CreditCard> findCreditCardsByUserName(String username) {
        return creditCardPersistence.findCreditCardsByUserName(username);
    }
}
