package es.upm.mabills.api.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class LoginDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
