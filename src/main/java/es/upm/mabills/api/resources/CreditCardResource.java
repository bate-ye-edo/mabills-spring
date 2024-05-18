package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(CreditCardResource.CREDIT_CARDS)
public class CreditCardResource {
    public static final String CREDIT_CARDS = "/credit-cards";
    public static final String DELETE_CREDIT_CARD = "/{uuid}";

    private final CreditCardService creditCardService;

    @Autowired
    public CreditCardResource(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping
    public List<CreditCard> getUserCreditCards(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return creditCardService.findCreditCardsForUser(userPrincipal);
    }

    @PostMapping
    public CreditCard createCreditCard(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated CreditCard creditCard) {
        return creditCardService.createCreditCard(userPrincipal, creditCard);
    }

    @DeleteMapping(DELETE_CREDIT_CARD)
    public void deleteCreditCard(@PathVariable("uuid") String uuid) {
        creditCardService.deleteCreditCard(uuid);
    }
}
