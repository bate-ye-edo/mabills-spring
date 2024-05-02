package es.upm.mabills.api.http_errors;

import es.upm.mabills.exceptions.ExpenseCategoryAlreadyExistsException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
        org.springframework.security.access.AccessDeniedException.class
    })
    @ResponseBody
    public String unauthorizedRequest(Exception exception) {
        LOGGER.debug(() -> "Unauthorized: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.UNAUTHORIZED.value()).toString();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        BadCredentialsException.class
    })
    @ResponseBody
    public ErrorMessage badCredentials(BadCredentialsException exception) {
        LOGGER.debug(() -> "Bad credentials: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
        UserAlreadyExistsException.class
    })
    @ResponseBody
    public ErrorMessage userAlreadyExists(UserAlreadyExistsException exception) {
        LOGGER.debug(() -> "User already exists: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.CONFLICT.value());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
        ExpenseCategoryAlreadyExistsException.class
    })
    @ResponseBody
    public ErrorMessage expenseCategoryAlreadyExists(ExpenseCategoryAlreadyExistsException exception) {
        LOGGER.debug(() -> "Expense category already exists: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.CONFLICT.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
    })
    @ResponseBody
    public ErrorMessage invalidArguments(MethodArgumentNotValidException exception) {
        LOGGER.debug(() -> "Invalid arguments: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value(), getErrorFieldNames(exception.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        HandlerMethodValidationException.class
    })
    @ResponseBody
    public ErrorMessage invalidArguments(HandlerMethodValidationException exception) {
        LOGGER.debug(() -> "Invalid arguments: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        UserNotFoundException.class,
        ExpenseCategoryNotFoundException.class
    })
    @ResponseBody
    public ErrorMessage notFoundException(RuntimeException exception) {
        LOGGER.debug(exception::getMessage);
        return new ErrorMessage(exception, HttpStatus.NOT_FOUND.value());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
        Exception.class
    })
    @ResponseBody
    public ErrorMessage exception(Exception exception) {
        LOGGER.error(() -> "Error: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private List<String> getErrorFieldNames(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .toList();
    }
}
