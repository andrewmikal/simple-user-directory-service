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
     * Creates an new EphemeralUserDirectory with a policy that accepts any username, any email, any screen name, and
     * any password.
     */
    public EphemeralUserDirectory() {
        _users = new HashMap<>();
        _passwords = new HashMap<>();
        // create new policy
        _policy = new Policy() {
            @Override
            public boolean checkUsername(String username) {
                return true;
            }

            @Override
            public boolean checkEmail(String email) {
                return true;
            }

            @Override
            public boolean checkScreenName(String screenName) {
                return true;
            }

            @Override
            public boolean checkPassword(String password) {
                return true;
            }
        };
    }

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

    /**
     * Removes the entries with the specified username as the key from the users and passwords hash maps.
     * @param username User name of the user to remove.
     * @return True if hasUser() returns true, false otherwise.
     */
    @Override
    public boolean removeUser(String username) {
        if (hasUser(username)) {
            _users.remove(username);
            _passwords.remove(username);
            return true;
        }
        return false;
    }

    /**
     * Retrieves an array of all the keys of the users hash map.
     * @return Returns the key set of the users hash map cast to an array of strings.
     */
    @Override
    public String[] getUsers() {
        return _users.keySet().toArray(new String[0]);
    }

    /**
     * Retrieves the directory's policy.
     * @return Retrieves the directory's policy.
     */
    @Override
    public Policy getPolicy() {
        return _policy;
    }

    /**
     * Checks that the provided password matches the password in the passwords hash map.
     * @param username Username of user to authenticate.
     * @param password Password used to authenticate the user.
     * @return Returns true if the passwords match, false otherwise.
     */
    @Override
    public boolean authenticateUser(String username, String password) {
        return password.equals(_passwords.get(username));
    }

    /**
     * Retrieves a UserData object from the users hash map containing information on the specified user.
     * @param username Username of the user to retrieve data on.
     * @return A UserData object containing the data if the user exists, and null if the user does not exist.
     */
    @Override
    public UserData getUserData(String username) {
        if (hasUser(username)) {
            return _users.get(username);
        }
        return null;
    }

    /**
     * If the directory has the specified user, then the directory removes the entry with the specified username as the
     * key from the users and passwords hash maps, then adds a new entry to each of the hash maps with the new username
     * as the key, using the old password, and creating a new UserData object with the new username and the old email
     * and screen name.
     * @param username Username of the user to update.
     * @param newUsername Username to change the user's current username to.
     */
    @Override
    public void updateUsername(String username, String newUsername) {
        if (hasUser(username)) {
            UserData data = _users.get(username);
            String pass = _passwords.get(username);
            _users.remove(username);
            _users.put(newUsername, new UserData(newUsername, data.getEmail(), data.getScreenName()));
            _passwords.remove(username);
            _passwords.put(newUsername, pass);
        }
    }

    /**
     * If the specified user exists, put the specified username back into the users hash map with a new UserData object
     * containing the old username, new email, and old screen name.
     * @param username Username of the user to update.
     * @param newEmail Email to change the user's current email to.
     */
    @Override
    public void updateEmail(String username, String newEmail) {
        if (hasUser(username)) {
            UserData data = _users.get(username);
            _users.put(username, new UserData(username, newEmail, data.getScreenName()));
        }
    }

    /**
     * If the specified user exists, put the specified username back into the users hash map with a new UserData object
     * containing the old username, odl email, and new screen name.
     * @param username Username of the user to update.
     * @param newScreenName Screen name to change the user's current screen name to.
     */
    @Override
    public void updateScreenName(String username, String newScreenName) {
        if (hasUser(username)) {
            UserData data = _users.get(username);
            _users.put(username, new UserData(username, data.getEmail(), newScreenName));
        }
    }

    /**
     * If the specified user exists, put the specified username back into the passwords hash map with the new password.
     * @param username Username of the user to update.
     * @param newPassword Password to change the user's current password to.
     */
    @Override
    public void updatePassword(String username, String newPassword) {
        if (hasUser(username)) {
            _passwords.put(username, newPassword);
        }
    }
}
