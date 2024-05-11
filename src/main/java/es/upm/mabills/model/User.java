package es.upm.mabills.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull
    private String username;
    private String password;
    @NotNull
    private String email;
    @NotNull
    private String mobile;
}
