package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    @Email
    private String email;
    @NonNull
    @Pattern(regexp = "^\\d+$")
    private String mobile;
}
