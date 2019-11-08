package com.ajmi.simpleuserdirectoryservice.user;

/**
 * Directory containing the user names, emails, scree names, and passwords of users.
 */
public interface UserDirectory {

    /**
     * Checks if the specified user exists in the directory.
     * @param username User name of the user to check for.
     * @return Returns true if the user was found in the directory, false otherwise.
     */
    boolean hasUser(String username);

    /**
     * Tries to add a user to the directory with the given user name, email, screen name, and password.
     * @param username User name of the new entry.
     * @param email Email of the new entry.
     * @param screeName Screen name of the new entry.
     * @param password Password of the new entry.
     */
    void addUser(String username, String email, String screeName, String password) throws UserAlreadyExistsException, PolicyFailureException;

    /**
     * Tries to remove the user with the given user name from the directory.
     * @param username User name of the user to remove.
     * @return Returns true if the user was removed, false if the user did not exist or could not be removed.
     */
    boolean removeUser(String username);

    /**
     * Retrieves a list of all the user names of users in the directory
     * @return Returns an array of strings, containing all users' user names.
     */
    String[] getUsers();

    /**
     * Retrieves the policy used to check the username, email, screen name, and password.
     * @return A non-null Policy.
     */
    Policy getPolicy();

    /**
     * Validate that the given username matches the given password in the user directory.
     * @param username Username of user to authenticate.
     * @param password Password used to authenticate the user.
     * @return Returns true if the username and password combination is valid, false otherwise.
     */
    boolean authenticateUser(String username, String password);

    /**
     * Retrieve data on the specified user.
     * @param username Username of the user to retrieve data on.
     * @return Returns a UserData object containing the user's data.
     */
    UserData getUserData(String username);

}
