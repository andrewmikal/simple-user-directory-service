package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Object containing retrievable data about a user.
 */
public class UserData {

    /** Username of the user. */
    private final String _username;
    /** Email of the user. */
    private final String _email;
    /** Screen name of the user. */
    private final String _screenName;

    /**
     * Create a new UserDirectory object from the provided information.
     * @param username Username of the user.
     * @param email Email of the user.
     * @param screenName Screen name of the user.
     */
    public UserData(String username, String email, String screenName) {
        _username = username;
        _email = email;
        _screenName = screenName;
    }

    /**
     * Get the username of the user.
     * @return Return the user's username.
     */
    public String getUsername() {
        return _username;
    }

    /**
     * Get the email of the user.
     * @return Return the user's email.
     */
    public String getEmail() {
        return _email;
    }

    /**
     * Get the screen name of the user.
     * @return Return the user's screen name.
     */
    public String getScreenName() {
        return _screenName;
    }
}
