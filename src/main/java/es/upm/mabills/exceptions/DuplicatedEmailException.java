package es.upm.mabills.exceptions;


public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super(String.format("Email '%s' is already in use", email));
    }
}
