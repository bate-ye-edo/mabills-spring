package es.upm.mabills.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
