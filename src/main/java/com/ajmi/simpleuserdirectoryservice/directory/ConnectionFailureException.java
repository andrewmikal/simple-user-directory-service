package com.ajmi.simpleuserdirectoryservice.directory;

/**
 * Exception thrown when a connection-related error occurs when using a User Directory.
 */
public class ConnectionFailureException extends UserDirectoryException{

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
    public ConnectionFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public ConnectionFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
