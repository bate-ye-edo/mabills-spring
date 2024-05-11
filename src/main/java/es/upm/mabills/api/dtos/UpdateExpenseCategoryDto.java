package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateExpenseCategoryDto(@NotNull String name) {
}