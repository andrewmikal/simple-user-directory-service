package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a password does not meet the requirements.
 */
public class InvalidPasswordException extends UserDirectoryException {
    public InvalidPasswordException(String message) {
        super(message);
    }
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
