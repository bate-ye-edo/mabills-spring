package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.services.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Rest
@RequestMapping(CreditCardResource.CREDIT_CARDS)
public class CreditCardResource {
    public static final String CREDIT_CARDS = "/credit-cards";

    private final CreditCardService creditCardService;

    @Autowired
    public CreditCardResource(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping
    public List<CreditCard> getUserCreditCards(@AuthenticationPrincipal String username) {
        return creditCardService.findCreditCardsByUserName(username);
    }
}
