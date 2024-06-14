package es.upm.mabills.api.http_errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {

    private String error;
    private String message;
    private Integer code;
    private List<String> errorFieldNames;
    ErrorMessage(Exception exception, Integer code) {
        this.error = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.code = code;
    }
    ErrorMessage(Exception exception, Integer code, List<String> errorFieldNames) {
        this.error = exception.getClass().getSimpleName();
        this.code = code;
        this.errorFieldNames = errorFieldNames;
        buildInvalidFieldsMessage();
    }
    private void buildInvalidFieldsMessage() {
        this.message = "Invalid Request: some fields are not valid. Fields: [";
        if (this.errorFieldNames != null) {
            this.message += String.join(", ", this.errorFieldNames);
        }
        this.message += "]";
    }
}
