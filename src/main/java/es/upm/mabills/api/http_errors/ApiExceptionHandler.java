package es.upm.mabills.api.http_errors;

import es.upm.mabills.exceptions.UserAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            org.springframework.security.access.AccessDeniedException.class
    })
    @ResponseBody
    public String unauthorizedRequest(Exception exception) {
        LogManager.getLogger(this.getClass()).debug(() -> "Unauthorized: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.UNAUTHORIZED.value()).toString();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadCredentialsException.class
    })
    @ResponseBody
    public ErrorMessage badCredentials(BadCredentialsException exception) {
        LogManager.getLogger(this.getClass()).debug(() -> "Bad credentials: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            UserAlreadyExistsException.class
    })
    @ResponseBody
    public ErrorMessage userAlreadyExists(UserAlreadyExistsException exception) {
        LogManager.getLogger(this.getClass()).debug(() -> "User already exists: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.CONFLICT.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    @ResponseBody
    public ErrorMessage invalidArguments(MethodArgumentNotValidException exception) {
        LogManager.getLogger(this.getClass()).debug(() -> "Invalid arguments: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value(), getErrorFieldNames(exception.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            Exception.class
    })
    @ResponseBody
    public ErrorMessage exception(Exception exception) {
        LogManager.getLogger(this.getClass()).error(() -> "Error: " + exception.getMessage());
        return new ErrorMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    private List<String> getErrorFieldNames(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .toList();
    }
}
