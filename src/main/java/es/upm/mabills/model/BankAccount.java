package es.upm.mabills.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    private static final String IBAN_REGEX = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$";
    private String uuid;

    @NotBlank
    @NotNull
    @Pattern(regexp = IBAN_REGEX)
    private String iban;
}
