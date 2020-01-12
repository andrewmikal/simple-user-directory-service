package com.ajmi.simpleuserdirectoryservice.data;

/**
 * Contains possible results from trying to authenticate a user.
 */
public enum Authentication {
    /** If the user was successfully authenticated. */
    VALID,
    /** If the given username did not belong to any users. */
    INVALID_USERNAME,
    /** If the given password was not the valid password for the specified user. */
    INVALID_PASSWORD
}
