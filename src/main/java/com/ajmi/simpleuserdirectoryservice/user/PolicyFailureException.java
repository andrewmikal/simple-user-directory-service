package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Exception thrown when the username, email, screen name, or password used to add a user to a UserDirectory does not
 * meet the corresponding policy.
 */
public class PolicyFailureException extends UserDirectoryException{
    private PolicyFailure _failure;

    /**
     * Constructs a new exception with the specified detail message and an undefined failure.
     * @param message the detail message.
     */
    public PolicyFailureException(String message) {
        super(message);
        _failure = PolicyFailure.UNDEFINED_FAILURE;
    }

    /**
     * Constructs a new exception with the specified detail message and failure.
     * @param message the detail message.
     * @param failure the failure.
     */
    public PolicyFailureException(String message, PolicyFailure failure) {
        super(message);
        _failure = failure;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public PolicyFailureException(String message, Throwable cause) {
        super(message, cause);
        _failure = PolicyFailure.UNDEFINED_FAILURE;
    }

    /**
     * Constructs a new exception with the specified detail message, cause, and failure.
     * @param message the detail message.
     * @param cause the cause.
     * @param failure the failure.
     */
    public PolicyFailureException(String message, Throwable cause, PolicyFailure failure) {
        super(message, cause);
        _failure = failure;
    }

    /**
     * Retrieves the policy that caused the failure.
     * @return a PolicyFailure enum indicating the failed policy.
     */
    public PolicyFailure getFailure() {
        return _failure;
    }
}
