package es.upm.mabills.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record FilterChartDto(@NotNull List<FilterDto> filterDtos, @NotNull String chartCategory, String chartGroupByType){}
