package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a screen name does not meet the requirements.
 */
public class InvalidScreenNameException extends UserDirectoryException {
    public InvalidScreenNameException(String message) {
        super(message);
    }
    public InvalidScreenNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
