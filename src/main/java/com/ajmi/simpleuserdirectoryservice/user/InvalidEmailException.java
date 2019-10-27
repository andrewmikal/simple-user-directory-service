package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when an email does not meet the requirements.
 */
public class InvalidEmailException extends UserDirectoryException {
    public InvalidEmailException(String message) {
        super(message);
    }
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
