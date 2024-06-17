package es.upm.mabills.exceptions;

public class MaBillsServiceException extends RuntimeException {
    public MaBillsServiceException() {
        super("An unexpected error occurred while processing the request");
    }

    public MaBillsServiceException(String message) {
        super(message);
    }
}
