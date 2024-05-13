package es.upm.mabills.api.dtos;

import es.upm.mabills.model.custom_validations.NumbersOnly;
import es.upm.mabills.model.custom_validations.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterDto (@NotNull String username, @NotNull @Password String password, @NotNull @Email String email, @NotNull @NumbersOnly String mobile) {
}
