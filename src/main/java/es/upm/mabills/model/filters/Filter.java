package es.upm.mabills.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private FilterField filterField;
    private FilterComparisons filterComparison;
    private String filterValue;
    private String secondFilterValue;
}
