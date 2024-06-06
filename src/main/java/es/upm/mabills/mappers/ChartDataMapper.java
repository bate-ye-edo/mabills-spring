package es.upm.mabills.mappers;

import es.upm.mabills.model.ChartData;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChartDataMapper {
    @Mapping(target = "name", source = "date", dateFormat = "dd-MM-yyyy")
    ChartData toChartData(DateChartData dateChartData);
}
