package es.upm.mabills.exceptions;

public class MaBillsUnexpectedException extends RuntimeException {
    public MaBillsUnexpectedException() {
        super("An unexpected error occurred while processing the request");
    }
}
