package es.upm.mabills.services;

import es.upm.mabills.mappers.CreditCardMapper;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.CreditCardPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardService {
    private final CreditCardPersistence creditCardPersistence;
    private final CreditCardMapper creditCardMapper;
    @Autowired
    public CreditCardService(CreditCardPersistence creditCardPersistence, CreditCardMapper creditCardMapper) {
        this.creditCardPersistence = creditCardPersistence;
        this.creditCardMapper = creditCardMapper;
    }

    public List<CreditCard> findCreditCardsByUserName(String username) {
        return creditCardPersistence.findCreditCardsByUserName(username)
                .stream()
                .map(creditCardMapper::toCreditCard)
                .toList();
    }
}