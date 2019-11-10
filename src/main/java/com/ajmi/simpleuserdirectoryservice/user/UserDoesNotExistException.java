package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a non-existent user is retrieved from a user directory.
 */
public class UserDoesNotExistException extends UserDirectoryException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
    public UserDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
