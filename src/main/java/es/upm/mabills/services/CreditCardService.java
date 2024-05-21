package es.upm.mabills.services;

import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.CreditCardMapper;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.CreditCardPersistence;
import es.upm.mabills.services.dependency_validators.DependencyValidator;
import es.upm.mabills.services.exception_mappers.EntityNotFoundExceptionMapper;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CreditCardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardService.class);
    private final CreditCardPersistence creditCardPersistence;
    private final CreditCardMapper creditCardMapper;
    private final DependencyValidator dependencyValidator;

    @Autowired
    public CreditCardService(CreditCardPersistence creditCardPersistence, CreditCardMapper creditCardMapper,
                             @Qualifier("creditCardDependencyValidator") DependencyValidator dependencyValidator) {
        this.creditCardPersistence = creditCardPersistence;
        this.creditCardMapper = creditCardMapper;
        this.dependencyValidator = dependencyValidator;
    }

    public List<CreditCard> findCreditCardsForUser(UserPrincipal user) {
        return creditCardPersistence.findCreditCardsForUser(user)
                .stream()
                .map(creditCardMapper::toCreditCard)
                .toList();
    }

    @Transactional
    public CreditCard createCreditCard(UserPrincipal user, CreditCard creditCard) {
        dependencyValidator.assertDependencies(user, creditCard);
        return Try.of(()->creditCardMapper.toCreditCard(creditCardPersistence.createCreditCard(user, creditCard)))
                .getOrElseThrow(EntityNotFoundExceptionMapper::map);
    }

    @Transactional
    public void deleteCreditCard(UserPrincipal userPrincipal, String uuid) {
        try{
            creditCardPersistence.deleteCreditCard(userPrincipal, uuid);
        } catch (CreditCardNotFoundException cEx) {
            LOGGER.error("Couldn't found credit card with id: {} for user: {}", uuid, userPrincipal.getUsername());
            throw cEx;
        } catch (Exception e) {
            throw new MaBillsServiceException();
        }
    }
}
