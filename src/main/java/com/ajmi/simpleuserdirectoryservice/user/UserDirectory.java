package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Directory containing the user names, emails, scree names, and passwords of users.
 */
public interface UserDirectory {

    /**
     * Checks if the specified user exists in the directory.
     * @param username User name of the user to check for.
     * @return Returns true if the user was found in the directory, false otherwise.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    boolean hasUser(String username) throws ConnectionFailureException;

    /**
     * Tries to add a user to the directory with the given user name, email, screen name, and password.
     * @param username User name of the new entry.
     * @param email Email of the new entry.
     * @param screeName Screen name of the new entry.
     * @param password Password of the new entry.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     * @throws UserAlreadyExistsException Thrown when a user with the specified username already exists in the directory.
     * @throws PolicyFailureException Thrown when one or more of the supplied arguments fail one of the directory's policies.
     */
    void addUser(String username, String email, String screeName, String password) throws ConnectionFailureException, UserAlreadyExistsException, PolicyFailureException;

    /**
     * Tries to remove the user with the given user name from the directory.
     * @param username User name of the user to remove.
     * @return Returns true if the user was removed, false if the user did not exist or could not be removed.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    boolean removeUser(String username) throws ConnectionFailureException;

    /**
     * Retrieves a list of all the user names of users in the directory
     * @return Returns an array of strings, containing all users' user names.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    String[] getUsers() throws ConnectionFailureException;

    /**
     * Retrieves the policy used to check the username, email, screen name, and password.
     * @return A non-null Policy.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    Policy getPolicy() throws ConnectionFailureException;

    /**
     * Sets the policy used to check the username, email, screen name, and password.
     * @param policy Policy to set the directory's policy to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void setPolicy(Policy policy) throws ConnectionFailureException;

    /**
     * Validate that the given username matches the given password in the user directory.
     * @param username Username of user to authenticate.
     * @param password Password used to authenticate the user.
     * @return Returns true if the username and password combination is valid, false otherwise.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    boolean authenticateUser(String username, String password) throws ConnectionFailureException;

    /**
     * Retrieve data on the specified user.
     * @param username Username of the user to retrieve data on.
     * @return Returns a UserData object containing the user's data.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    UserData getUserData(String username) throws ConnectionFailureException;

    /**
     * Updates the username of the specified user.
     * @param username Username of the user to update.
     * @param newUsername Username to change the user's current username to.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    void updateUsername(String username, String newUsername) throws ConnectionFailureException;

    /**
     * Updates the email of the specified user.
     * @param username Username of the user to update.
     * @param newEmail Email to change the user's current email to.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    void updateEmail(String username, String newEmail) throws ConnectionFailureException;

    /**
     * Updates the screen name of the specified user.
     * @param username Username of the user to update.
     * @param newScreenName Screen name to change the user's current screen name to.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    void updateScreenName(String username, String newScreenName) throws ConnectionFailureException;

    /**
     * Updates the password of the specified user.
     * @param username Username of the user to update.
     * @param newPassword Password to change the user's current password to.
     * @throws ConnectionFailureException Thrown when a connection-related error occurs.
     */
    void updatePassword(String username, String newPassword) throws ConnectionFailureException;

}
