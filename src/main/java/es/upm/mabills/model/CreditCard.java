package es.upm.mabills.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCard {
    private static final String CREDIT_CARD_NUMBER_PATTERN = "^\\d{15,16}$";
    @NotNull
    @NotBlank
    @Pattern(regexp = CREDIT_CARD_NUMBER_PATTERN)
    private String creditCardNumber;

    private String uuid;

    private BankAccount bankAccount;
}
