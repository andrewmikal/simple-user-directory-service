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
     * @return Returns true if the the the entry was successfully added, false otherwise.
     */
    boolean addUser(String username, String email, String screeName, String password) throws
            UserAlreadyExistsException,
            InvalidEmailException,
            InvalidPasswordException,
            InvalidScreenNameException,
            InvalidUsernameException;

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

}
