package com.ajmi.simpleuserdirectoryservice.user;

import java.util.HashMap;

/**
 * User directory implemented as a Plain Old Java Object.
 */
public class EphemeralUserDirectory implements UserDirectory {

    /** Hash map containing user data indexed by username. **/
    private HashMap<String, UserData> _users;
    /** Hash map containing user passwords indexed by username. **/
    private HashMap<String, String> _passwords;
    /** Policy for usernames, emails, screen names, and passwords. */
    private Policy _policy;

    /**
     * Checks that the users hash map contains the provided username as a key.
     * @param username User name of the user to check for.
     * @return Returns true if the provided username is a key in the hash map, false otherwise.
     */
    @Override
    public boolean hasUser(String username) {
        return _users.containsKey(username);
    }

    /**
     * Adds the username, email, and screen name to the users hash map, and the password to the passwords hash map.
     * @param username User name of the new entry.
     * @param email Email of the new entry.
     * @param screeName Screen name of the new entry.
     * @param password Password of the new entry.
     * @throws UserAlreadyExistsException Thrown if the hasUser() method returns true for the provided username.
     * @throws PolicyFailureException Thrown if the parameters do not pass every check by the directory's policy.
     */
    @Override
    public void addUser(String username, String email, String screeName, String password) throws UserAlreadyExistsException, PolicyFailureException {
        // make sure the user does not already exist
        if (_users.containsKey(username)) {
            throw new UserAlreadyExistsException("A user with username \"" + username + "\" already exists.");
        }
        // check that the parameters meet the policy's requirements
        if (!(_policy.checkUsername(username) && _policy.checkEmail(email) && _policy.checkScreenName(screeName) && _policy.checkPassword(password))) {
            throw new PolicyFailureException("The entered data failed the directory's policy.");
        }
        _users.put(username, new UserData(username, email, screeName));
        _passwords.put(username, password);
    }

    @Override
    public boolean removeUser(String username) {
        return false;
    }

    @Override
    public String[] getUsers() {
        return new String[0];
    }

    @Override
    public Policy getPolicy() {
        return null;
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return false;
    }

    @Override
    public UserData getUserData(String username) {
        return null;
    }

    @Override
    public void updateUsername(String username, String newUsername) {

    }

    @Override
    public void updateEmail(String username, String newEmail) {

    }

    @Override
    public void updateScreenName(String username, String newScreenName) {

    }

    @Override
    public void updatePassword(String username, String newPassword) {

    }
}
