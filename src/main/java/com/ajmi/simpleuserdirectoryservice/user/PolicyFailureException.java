package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when the username, email, screen name, or password used to add a user to a UserDirectory does not
 * meet the corresponding policy.
 */
public class PolicyFailureException extends UserDirectoryException{
    public PolicyFailureException(String message) {
        super(message);
    }

    public PolicyFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
