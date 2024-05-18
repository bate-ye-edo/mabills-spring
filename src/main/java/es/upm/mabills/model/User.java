package es.upm.mabills.model;

import es.upm.mabills.model.custom_validations.NumbersOnly;
import es.upm.mabills.model.custom_validations.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;

    @Password
    @Setter
    private String password;

    @NotNull
    @Email
    @NotBlank
    private String email;

    @NotNull
    @NumbersOnly
    @NotBlank
    private String mobile;

    List<BankAccount> bankAccounts;
}
