package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception that can be thrown by UserDirectory.
 */
public abstract class UserDirectoryException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
    public UserDirectoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public UserDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
