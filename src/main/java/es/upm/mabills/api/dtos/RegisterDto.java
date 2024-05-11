package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegisterDto (@NotNull String username, @NotNull String password, @NotNull @Email String email, @NotNull @Pattern(regexp = "^\\d+$") String mobile) {
}
