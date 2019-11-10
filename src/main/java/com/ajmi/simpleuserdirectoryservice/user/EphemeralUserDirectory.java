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

    @Override
    public boolean hasUser(String username) {
        return _users.containsKey(username);
    }

    @Override
    public void addUser(String username, String email, String screeName, String password) throws UserAlreadyExistsException, PolicyFailureException {

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
