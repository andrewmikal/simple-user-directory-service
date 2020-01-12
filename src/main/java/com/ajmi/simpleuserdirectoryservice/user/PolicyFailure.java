package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Enum indicating what policy failed in a PolicyFailureException.
 */
public enum PolicyFailure {
    /** The cause of the policy failure has not been defined. */
    UNDEFINED_CAUSE,
    /** The username failed the policy. */
    ILLEGAL_USERNAME,
    /** The email failed the policy. */
    ILLEGAL_EMAIL,
    /** The screen name failed the policy. */
    ILLEGAL_SCREEN_NAME,
    /** The password failed the policy. */
    ILLEGAL_PASSWORD
}
