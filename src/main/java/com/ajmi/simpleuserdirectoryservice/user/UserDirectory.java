package com.ajmi.simpleuserdirectoryservice.user;

import java.util.Optional;

/**
 * Directory containing the user names, emails, scree names, and passwords of users.
 */
public interface UserDirectory {

    /**
     * Checks if the specified user exists in the directory.
     * @param username the user name of the user to check for.
     * @return true if the user was found in the directory, false otherwise.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    boolean hasUser(String username) throws ConnectionFailureException;

    /**
     * Tries to add a user to the directory with the given user name, email, screen name, and password.
     * @param username the user name of the new entry.
     * @param email the email of the new entry.
     * @param screeName the screen name of the new entry.
     * @param password the password of the new entry.
     * @throws ConnectionFailureException if a connection-related error occurs.
     * @throws UserAlreadyExistsException if a user with the specified username already exists in the directory.
     * @throws PolicyFailureException if one or more of the supplied arguments fail one of the directory's policies.
     */
    void addUser(String username, String email, String screeName, String password) throws ConnectionFailureException, UserAlreadyExistsException, PolicyFailureException;

    /**
     * Tries to remove the user with the given user name from the directory.
     * @param username the user name of the user to remove.
     * @return true if the user was removed, false if the user did not exist or could not be removed.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    boolean removeUser(String username) throws ConnectionFailureException;

    /**
     * Retrieves a list of all the user names of users in the directory
     * @return an array of strings, containing all users' user names.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    String[] getUsers() throws ConnectionFailureException;

    /**
     * Retrieves the policy used to check the username, email, screen name, and password.
     * @return a non-null Policy.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    Policy getPolicy() throws ConnectionFailureException;

    /**
     * Sets the policy used to check the username, email, screen name, and password.
     * @param policy the Policy to set the directory's policy to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void setPolicy(Policy policy) throws ConnectionFailureException;

    /**
     * Validate that the given username matches the given password in the user directory.
     * @param username the username of user to authenticate.
     * @param password the password used to authenticate the user.
     * @return true if the username and password combination is valid, false otherwise.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    boolean authenticateUser(String username, String password) throws ConnectionFailureException;

    /**
     * Validate that the given username matches the given password in the user directory.
     * @param username of user to authenticate.
     * @param password used to authenticate the user.
     * @return an Authentication enum indicating if the username password combination was valid, and if not, what field
     * caused the authentication failure.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    Authentication authenticateUserDetailed(String username, String password) throws ConnectionFailureException;

    /**
     * Retrieve data on the specified user.
     * @param username the username of the user to retrieve data on.
     * @return an Optional<UserData> object containing the user's data if the user exists.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    Optional<UserData> getUserData(String username) throws ConnectionFailureException;

    /**
     * Updates the username of the specified user.
     * @param username the username of the user to update.
     * @param newUsername the username to change the user's current username to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void updateUsername(String username, String newUsername) throws ConnectionFailureException;

    /**
     * Updates the email of the specified user.
     * @param username the username of the user to update.
     * @param newEmail the email to change the user's current email to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void updateEmail(String username, String newEmail) throws ConnectionFailureException;

    /**
     * Updates the screen name of the specified user.
     * @param username the username of the user to update.
     * @param newScreenName the screen name to change the user's current screen name to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void updateScreenName(String username, String newScreenName) throws ConnectionFailureException;

    /**
     * Updates the password of the specified user.
     * @param username the username of the user to update.
     * @param newPassword the password to change the user's current password to.
     * @throws ConnectionFailureException if a connection-related error occurs.
     */
    void updatePassword(String username, String newPassword) throws ConnectionFailureException;

}
