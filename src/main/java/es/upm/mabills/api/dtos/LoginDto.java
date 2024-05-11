package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LoginDto(@NotNull String username, @NotNull String password) {
}
