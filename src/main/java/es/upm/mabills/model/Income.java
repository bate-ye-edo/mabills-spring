package es.upm.mabills.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Income {
    private String uuid;
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Timestamp incomeDate;
    private CreditCard creditCard;
    private BankAccount bankAccount;
}
