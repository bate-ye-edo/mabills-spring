package es.upm.mabills.services.exception_mappers;

import es.upm.mabills.exceptions.InvalidRequestException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.InvalidDataAccessApiUsageException;

public class FilterExceptionMapper {
    private static final Logger LOGGER = LogManager.getLogger(FilterExceptionMapper.class);
    public static RuntimeException map(Throwable e) {
        LOGGER.error("Mapping exception: {}", e.getMessage());
        if(e.getClass().equals(InvalidDataAccessApiUsageException.class)){
            return new InvalidRequestException("Filter's value is invalid.");
        }
        return new MaBillsServiceException("Some error occurred while applying filters.");
    }
    private FilterExceptionMapper() {}
}
