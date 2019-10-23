package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Directory containing the user names, emails, scree names, and passwords of users.
 */
public interface UserDirectory {

    /**
     * Checks if the specified user exists in the directory.
     * @param username of the user to check for.
     * @return true if the user was found in the directory, false otherwise.
     */
    boolean has(String username);

}
