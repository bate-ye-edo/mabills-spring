package es.upm.mabills.mappers;

import es.upm.mabills.api.dtos.FilterDto;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterComparisons;
import es.upm.mabills.model.filters.FilterField;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FilterMapper {
    @Mapping(target = "filterField", expression = "java(toFilterField(filterDto.filterField()))")
    @Mapping(target = "filterComparison", expression = "java(toFilterComparisons(filterDto.filterComparison()))")
    Filter toFilter(FilterDto filterDto);

    default FilterField toFilterField(String filterField) {
        return FilterField.valueOf(filterField);
    }

    default FilterComparisons toFilterComparisons(String filterComparison) {
        return FilterComparisons.valueOf(filterComparison);
    }
}
