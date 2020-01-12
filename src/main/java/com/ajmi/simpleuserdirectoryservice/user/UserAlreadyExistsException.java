package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a user in the UserDirectory already exists.
 */
public class UserAlreadyExistsException extends UserDirectoryException {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
