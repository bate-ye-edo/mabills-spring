package es.upm.mabills.services.charts;

import es.upm.mabills.model.Chart;
import es.upm.mabills.model.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface ChartService {
    Chart getChart(UserPrincipal userPrincipal, String groupBy);
}
