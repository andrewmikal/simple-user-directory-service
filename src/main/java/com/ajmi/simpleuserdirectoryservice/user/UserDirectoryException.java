package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception that can be thrown by UserDirectory.
 */
public abstract class UserDirectoryException extends Exception {

    public UserDirectoryException(String message) {
        super(message);
    }

    public UserDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
