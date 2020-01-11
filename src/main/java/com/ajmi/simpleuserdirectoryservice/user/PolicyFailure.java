package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Enum indicating what policy failed in a PolicyFailureException.
 */
public enum PolicyFailure {
    UNDEFINED,
    ILLEGAL_USERNAME,
    ILLEGAL_EMAIL,
    ILLEGAL_SCREEN_NAME,
    ILLEGAL_PASSWORD
}
