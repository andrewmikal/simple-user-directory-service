package com.ajmi.simpleuserdirectoryservice.directory;

import com.ajmi.simpleuserdirectoryservice.cryptography.PasswordCrypt;
import com.ajmi.simpleuserdirectoryservice.data.Authentication;
import com.ajmi.simpleuserdirectoryservice.data.UserData;
import com.ajmi.simpleuserdirectoryservice.data.PolicyFailure;

import java.util.HashMap;
import java.util.Optional;

/**
 * User directory implemented as a Plain Old Java Object.
 */
public class EphemeralUserDirectory implements UserDirectory {

    /** Message used for exceptions caused by a failed policy. */
    private static final String POLICY_FAILURE_MSG = "The entered data failed the directory's policy.";

    /** Hash map containing user data indexed by username. **/
    private HashMap<String, UserData> _users;
    /** Hash map containing user passwords indexed by username. **/
    private HashMap<String, String> _passwords;
    /** Hash map containing user salts indexed by username. **/
    private HashMap<String, String> _salts;
    /** Policy for usernames, emails, screen names, and passwords. */
    private Policy _policy;

    /**
     * Creates an new EphemeralUserDirectory with a policy that accepts any username, any email, any screen name, and
     * any password.
     */
    public EphemeralUserDirectory() {
        _users = new HashMap<>();
        _passwords = new HashMap<>();
        _salts = new HashMap<>();

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
     * @param username the user name of the user to check for.
     * @return true if the provided username is a key in the hash map, false otherwise.
     */
    @Override
    public boolean hasUser(String username) {
        return _users.containsKey(username);
    }

    /**
     * Adds the username, email, and screen name to the users hash map, and the password to the passwords hash map.
     * @param username the user name of the new entry.
     * @param email the email of the new entry.
     * @param screeName the screen name of the new entry.
     * @param password the password of the new entry.
     * @throws UserAlreadyExistsException if the hasUser() method returns true for the provided username.
     * @throws PolicyFailureException if the parameters do not pass every check by the directory's policy.
     */
    @Override
    public void addUser(String username, String email, String screeName, String password) throws UserAlreadyExistsException, PolicyFailureException {
        // make sure the user does not already exist
        if (_users.containsKey(username)) {
            throw new UserAlreadyExistsException("A user with username \"" + username + "\" already exists.");
        }
        // check that the parameters meet the policy's requirements
        if (!_policy.checkUsername(username)) {
            throw new PolicyFailureException(POLICY_FAILURE_MSG, PolicyFailure.ILLEGAL_USERNAME);
        }
        if (!_policy.checkEmail(email)) {
            throw new PolicyFailureException(POLICY_FAILURE_MSG, PolicyFailure.ILLEGAL_EMAIL);
        }
        if (!_policy.checkScreenName(screeName)) {
            throw new PolicyFailureException(POLICY_FAILURE_MSG, PolicyFailure.ILLEGAL_SCREEN_NAME);
        }
        if (!_policy.checkPassword(password)) {
            throw new PolicyFailureException(POLICY_FAILURE_MSG, PolicyFailure.ILLEGAL_PASSWORD);
        }

        _users.put(username, new UserData(username, email, screeName));

        String salt = PasswordCrypt.nextSalt();
        _passwords.put(username, PasswordCrypt.hashPassword(password, salt));
        _salts.put(username, salt);
    }

    /**
     * Removes the entries with the specified username as the key from the users and passwords hash maps.
     * @param username the user name of the user to remove.
     * @return true if hasUser() returns true, false otherwise.
     */
    @Override
    public boolean removeUser(String username) {
        if (hasUser(username)) {
            _users.remove(username);
            _passwords.remove(username);
            _salts.remove(username);
            return true;
        }
        return false;
    }

    /**
     * Retrieves an array of all the keys of the users hash map.
     * @return the key set of the users hash map cast to an array of strings.
     */
    @Override
    public String[] getUsers() {
        String[] users = new String[_users.size()];
        _users.keySet().toArray(users);
        return users;
    }

    /**
     * Retrieves the directory's policy.
     * @return the directory's policy.
     */
    @Override
    public Policy getPolicy() {
        return _policy;
    }

    /**
     * Sets the directory's internal policy field.
     * @param policy the Policy to set the directory's policy to.
     */
    @Override
    public void setPolicy(Policy policy) {
        _policy = policy;
    }

    /**
     * Checks that the provided password matches the password in the passwords hash map.
     * @param username the username of user to authenticate.
     * @param password the password used to authenticate the user.
     * @return Returns true if the passwords match, false otherwise.
     */
    @Override
    public boolean authenticateUser(String username, String password) {
        return authenticateUserDetailed(username, password) == Authentication.VALID;
    }

    /**
     * Checks that the provided password matches the password in the passwords hash map.
     * @param username the username of user to authenticate.
     * @param password the password used to authenticate the user.
     * @return INVALID_USERNAME if the directory doesn't have the specified user, INVALID_PASSWORD if the given password
     * and the password in the passwords hash map don't match, and VALID if they do.
     */
    @Override
    public Authentication authenticateUserDetailed(String username, String password) {
        Authentication authentication;
        if (hasUser(username)) {
            if (PasswordCrypt.hashPassword(password, _salts.get(username)).equals(_passwords.get(username))) {
                authentication = Authentication.VALID;
            } else {
                authentication = Authentication.INVALID_PASSWORD;
            }
        } else {
            authentication = Authentication.INVALID_USERNAME;
        }
        return authentication;
    }

    /**
     * Retrieves a UserData object from the users hash map containing information on the specified user.
     * @param username the username of the user to retrieve data on.
     * @return a UserData object containing the data if the user exists, and null if the user does not exist.
     */
    @Override
    public Optional<UserData> getUserData(String username) {
        if (hasUser(username)) {
            return Optional.of(_users.get(username));
        }
        return Optional.empty();
    }

    /**
     * If the directory has the specified user, then the directory removes the entry with the specified username as the
     * key from the users and passwords hash maps, then adds a new entry to each of the hash maps with the new username
     * as the key, using the old password, and creating a new UserData object with the new username and the old email
     * and screen name.
     * @param username the username of the user to update.
     * @param newUsername the username to change the user's current username to.
     */
    @Override
    public void updateUsername(String username, String newUsername) {
        if (hasUser(username)) {
            UserData data = _users.get(username);
            String pass = _passwords.get(username);
            String salt = _salts.get(username);
            _users.remove(username);
            _users.put(newUsername, new UserData(newUsername, data.getEmail(), data.getScreenName()));
            _passwords.remove(username);
            _passwords.put(newUsername, pass);
            _salts.remove(username);
            _salts.put(newUsername, salt);
        }
    }

    /**
     * If the specified user exists, put the specified username back into the users hash map with a new UserData object
     * containing the old username, new email, and old screen name.
     * @param username the username of the user to update.
     * @param newEmail the email to change the user's current email to.
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
     * @param username the username of the user to update.
     * @param newScreenName the screen name to change the user's current screen name to.
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
     * @param username the username of the user to update.
     * @param newPassword the password to change the user's current password to.
     */
    @Override
    public void updatePassword(String username, String newPassword) {
        if (hasUser(username)) {
            String salt = PasswordCrypt.nextSalt();
            _passwords.put(username, PasswordCrypt.hashPassword(newPassword, salt));
            _salts.put(username, salt);
        }
    }
}
