package es.upm.mabills.persistence.chart_data_dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class DateChartData {
    private Timestamp date;
    private BigDecimal value;
}
