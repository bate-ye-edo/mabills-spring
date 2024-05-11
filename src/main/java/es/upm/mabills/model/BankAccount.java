package es.upm.mabills.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    private String uuid;
    private String iban;
    private User user;
    private List<CreditCard> creditCards;
}
