package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a user in the UserDirectory already exists.
 */
public class UserAlreadyExistsException extends UserDirectoryException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
