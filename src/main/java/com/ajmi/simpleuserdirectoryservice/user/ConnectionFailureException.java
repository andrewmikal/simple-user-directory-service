package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when a connection-related error occurs when using a User Directory.
 */
public class ConnectionFailureException extends UserDirectoryException{
    public ConnectionFailureException(String message) {
        super(message);
    }

    public ConnectionFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
