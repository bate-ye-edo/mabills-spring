package es.upm.mabills.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityAndFieldName {
    private String entityName;
    private String fieldName;
}
