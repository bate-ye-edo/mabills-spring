package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FilterDto(@NotNull String filterField, @NotNull String filterComparison, @NotNull String filterValue, String secondFilterValue){}
