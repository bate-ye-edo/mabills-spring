package es.upm.mabills.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND = "User not found";
    public UserNotFoundException(String username) {
        super("User with username " + username + " not found");
    }

    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
