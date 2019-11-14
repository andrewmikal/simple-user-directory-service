package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Determines whether usernames, emails, screen names, and passwords are valid.
 */
public interface Policy {

    /**
     * Checks if the specified username is valid.
     * @param username Username to check.
     * @return Returns true if the username is valid, false otherwise.
     */
    boolean checkUsername(String username);

    /**
     * Checks if the specified email is valid.
     * @param email Email to check.
     * @return Returns true if the email is valid, false otherwise.
     */
    boolean checkEmail(String email);

    /**
     * Checks if the specified screen name is valid.
     * @param screenName Screen name to check.
     * @return Returns true if the screen name is valid, false otherwise.
     */
    boolean checkScreenName(String screenName);

    /**
     * Checks if the specified password is valid.
     * @param password Password to check.
     * @return Returns true if the screen name is valid, false otherwise.
     */
    boolean checkPassword(String password);

}
