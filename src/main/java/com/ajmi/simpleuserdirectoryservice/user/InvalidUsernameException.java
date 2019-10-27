package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a user name does not meet the requirements.
 */
public class InvalidUsernameException extends UserDirectoryException {
    public InvalidUsernameException(String message) {
        super(message);
    }
    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
