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
     * @param username the username of the user.
     * @param email the email of the user.
     * @param screenName the screen name of the user.
     */
    public UserData(String username, String email, String screenName) {
        _username = username;
        _email = email;
        _screenName = screenName;
    }

    /**
     * Get the username of the user.
     * @return the user's username.
     */
    public String getUsername() {
        return _username;
    }

    /**
     * Get the email of the user.
     * @return the user's email.
     */
    public String getEmail() {
        return _email;
    }

    /**
     * Get the screen name of the user.
     * @return the user's screen name.
     */
    public String getScreenName() {
        return _screenName;
    }

    /**
     * Checks if this UserData object has the same user information as another object.
     * @param obj the Object to check for the same user information.
     * @return true if both objects have the same user information, false if not, or if one isn't a UserData.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserData) {
            UserData that = (UserData) obj;
            // equal if username, email, and screen name are equal
            return _username.equals(that._username) &&
                    _email.equals(that._email) &&
                    _screenName.equals(that._screenName);
        }
        // not equal if obj is not a UserData
        return false;
    }
}
