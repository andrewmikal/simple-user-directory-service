package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Determines whether usernames, emails, screen names, and passwords are valid.
 */
public interface Policy {

    /**
     * Checks if the specified username is valid.
     * @param username the username to check.
     * @return true if the username is valid, false otherwise.
     */
    boolean checkUsername(String username);

    /**
     * Checks if the specified email is valid.
     * @param email the email to check.
     * @return true if the email is valid, false otherwise.
     */
    boolean checkEmail(String email);

    /**
     * Checks if the specified screen name is valid.
     * @param screenName the screen name to check.
     * @return true if the screen name is valid, false otherwise.
     */
    boolean checkScreenName(String screenName);

    /**
     * Checks if the specified password is valid.
     * @param password the password to check.
     * @return true if the screen name is valid, false otherwise.
     */
    boolean checkPassword(String password);

}
