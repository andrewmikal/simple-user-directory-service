package com.ajmi.simpleuserdirectoryservice.tests.directory;

import com.ajmi.simpleuserdirectoryservice.data.Authentication;
import com.ajmi.simpleuserdirectoryservice.data.UserData;
import com.ajmi.simpleuserdirectoryservice.directory.Policy;
import com.ajmi.simpleuserdirectoryservice.data.PolicyFailure;
import com.ajmi.simpleuserdirectoryservice.directory.PolicyFailureException;
import com.ajmi.simpleuserdirectoryservice.directory.*;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static junit.framework.TestCase.*;

/**
 * Tests for the UserDirectory interface.
 */
public abstract class TestUserDirectory {

    /** Used for generating unique user names. */
    private static int userNumber = 0;

    /**
     * Creates a username created from the current time.
     * @return a String to use as a username based on the current time.
     */
    private static synchronized String username() {
        return "TestUserDirectory-Username:"+System.currentTimeMillis()+"-"+userNumber++;
    }

    /**
     * Creates a new user directory.
     * @return a new UserDirectory object.
     */
    protected abstract UserDirectory create();

    private ArrayList<String> usersToRemove = new ArrayList<>();

    @After
    public void tearDown() {
        UserDirectory ud = create();
        for (String name : usersToRemove) {
            try {
                ud.removeUser(name);
            } catch (ConnectionFailureException e) {
                // ignore exception
            }
        }
    }

    /**
     * Tests the hasUser() method when the user does not exist in the directory.
     */
    @Test
    public void testHasUserDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        // check if the directory hasUser a user that doesn't exist
        assertFalse(ud.hasUser(username()));
    }

    /**
     * Tests the addUser() method when the user does not already exist in the directory and the hasUser() method when
     *  the user is added to the directory.
     */
    @Test
    public void testAddUserHasUser() throws ConnectionFailureException {
        UserDirectory ud = create();
        String uname = username();

        assertFalse(ud.hasUser(uname));
        try {
            ud.addUser(uname, "foo", "bar", "baz");
            removeUserLater(uname);
        } catch (UserDirectoryException e) {
            // unexpected exception
            fail("Unexpected UserDirectoryException");
        }
        assertTrue(ud.hasUser(uname));
    }

    /**
     * Tests that the addUser() method throws a UserAlreadyExists exception when the user already exists.
     * @throws ConnectionFailureException
     */
    @Test
    public void testAddUserExists() throws ConnectionFailureException {
        UserDirectory ud = create();
        String uname = username();

        assertFalse(ud.hasUser(uname));
        try {
            ud.addUser(uname, "foo", "bar", "baz");
            removeUserLater(uname);
        } catch (UserDirectoryException e) {
            // unexpected exception
            fail("Unexpected UserDirectory Exception");
        }
        assertTrue(ud.hasUser(uname));
        try {
            ud.addUser(uname, "foo", "bar", "baz");
            removeUserLater(uname);
            // exception was not caught
            fail("Failed to throw a UserAlreadyExistsException");
        } catch (UserAlreadyExistsException e) {
            // expected exception
            assertTrue(true);
        } catch (UserDirectoryException e) {
            // unexpected exception
        }
    }

    /**
     * Tests the removeUser() method when the user does not exist in the directory.
     */
    @Test
    public void testRemoveUserDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        assertFalse(ud.removeUser(username()));
    }

    /**
     * Tests the removeUser() method when the user exists in the directory.
     */
    @Test
    public void testRemoveUserExists() throws ConnectionFailureException {
        UserDirectory ud = create();
        String uname = username();
        try {
            ud.addUser(uname, "foo", "bar", "baz");
            removeUserLater(uname);
        } catch (UserDirectoryException e) {
            // unexpected exception
            fail("Unexpected UserDirectoryException");
        }

        assertTrue(ud.hasUser(uname));
        assertTrue(ud.removeUser(uname));
        assertFalse(ud.hasUser(uname));
    }

    /**
     * Tests the getUsers() method.
     */
    @Test
    public void testGetUsers() throws ConnectionFailureException {
        UserDirectory ud = create();
        String[] usernames = {"1"+username(), "2"+username(), "3"+username(), "4"+username(), "5"+username()};
        HashSet userSet = new HashSet<>(Arrays.asList(usernames));
        for (String name : usernames) {
            try {
                ud.addUser(name, "foo", "bar", "baz");
                removeUserLater(name);
            } catch (UserDirectoryException e) {
                // unexpected exception
                fail("Unexpected UserDirectoryException");
            }
        }
        String[] users = ud.getUsers();
        Arrays.sort(usernames);
        Arrays.sort(users);
        // filter out users not created by this test case
        String[] filteredUsers = new String[userSet.size()];
        int filteredUserIndex = 0;
        for (String user : users) {
            if (userSet.contains(user)) {
                filteredUsers[filteredUserIndex++] = user;
                userSet.remove(user);
            }
        }
        // assert that the two arrays of usernames are equal
        assertEquals(usernames.length, filteredUsers.length);
        for (int i = 0; i < usernames.length; i++) {
            assertEquals(usernames[i], filteredUsers[i]);
        }
    }

    /**
     * Tests that the getPolicy() method does not return null.
     */
    @Test
    public void testGetPolicyNotNull() throws ConnectionFailureException {
        UserDirectory ud = create();
        assertNotNull(ud.getPolicy());
    }

    /**
     * Test authenticateUser().
     */
    @Test
    public void testAuthenticateUser() throws ConnectionFailureException {
        UserDirectory ud = create();

        // create user
        String user = username();
        String pass = "password"+username();
        try {
            ud.addUser(user, "foo", "bar", pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }

        // test method when valid
        assertTrue(ud.authenticateUser(user, pass));

        // test method when invalid
        assertFalse(ud.authenticateUser(user, "wrong pass"));

        // test method when user doesn't exist
        String dneUser = "this-user-doesn't-exist"+username();
        assertFalse(ud.hasUser(dneUser));
        assertFalse(ud.authenticateUser(dneUser, pass));
    }

    /**
     * Tests getUserData().
     */
    @Test
    public void testGetUserData() throws ConnectionFailureException {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";

        try {
            ud.addUser(user, email, screen, "baz");
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }

        Optional<UserData> data = ud.getUserData(user);
        assertTrue(data.isPresent());
        assertEquals(new UserData(user, email, screen), data.get());
    }

    /**
     * Tests updateUsername().
     */
    @Test
    public void testUpdateUsername() throws ConnectionFailureException {
        UserDirectory ud = create();

        String user = "old"+username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newUser = "new"+username();

        try {
            ud.addUser(user, email, screen, pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }
        assertTrue(ud.hasUser(user));

        ud.updateUsername(user, newUser);
        removeUserLater(newUser);
        assertFalse(ud.hasUser(user));
        assertTrue(ud.hasUser(newUser));

        Optional<UserData> data = ud.getUserData(newUser);
        assertTrue(data.isPresent());
        assertEquals(new UserData(newUser, email, screen), data.get());

        assertTrue(ud.authenticateUser(newUser, pass));
    }

    /**
     * Tests updateUsername() when the specified user doesn't exist.
     */
    @Test
    public void testUpdateUsernameDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        ud.updateUsername(username()+"thisshoudln'texist", username()+"thisshoudln'tbehere");
    }

    /**
     * Tests updateEmail().
     */
    @Test
    public void testUpdateEmail() throws ConnectionFailureException {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newEmail = "qux";

        try {
            ud.addUser(user, email, screen, pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }
        ud.updateEmail(user, newEmail);

        Optional<UserData> data = ud.getUserData(user);
        assertTrue(data.isPresent());
        assertEquals(new UserData(user, newEmail, screen), data.get());

        assertTrue(ud.authenticateUser(user, pass));
    }

    /**
     * Tests updateEmail() when the specified user doesn't exist.
     */
    @Test
    public void testUpdateEmailDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        ud.updateEmail(username()+"thisshoudln'texist", username()+"thisshoudln'tbehere");
    }

    /**
     * Tests updateScreenName().
     */
    @Test
    public void testUpdateScreenName() throws ConnectionFailureException {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newScreen = "qux";

        try {
            ud.addUser(user, email, screen, pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }
        ud.updateScreenName(user, newScreen);

        Optional<UserData> data = ud.getUserData(user);
        assertTrue(data.isPresent());
        assertEquals(new UserData(user, email, newScreen), data.get());

        assertTrue(ud.authenticateUser(user, pass));
    }

    /**
     * Tests updateScreenName() when the specified user doesn't exist.
     */
    @Test
    public void testUpdateScreenNameDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        ud.updateScreenName(username()+"thisshoudln'texist", username()+"thisshoudln'tbehere");
    }

    /**
     * Tests updatePassword().
     */
    @Test
    public void testUpdatePassword() throws ConnectionFailureException {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newPass = "qux";

        try {
            ud.addUser(user, email, screen, pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }
        ud.updatePassword(user, newPass);

        Optional<UserData> data = ud.getUserData(user);
        assertTrue(data.isPresent());
        assertEquals(new UserData(user, email, screen), data.get());

        assertTrue(ud.authenticateUser(user, newPass));
    }

    /**
     * Tests updatePassword() when the specified user doesn't exist.
     */
    @Test
    public void testUpdatePasswordDNE() throws ConnectionFailureException {
        UserDirectory ud = create();
        ud.updatePassword(username()+"thisshoudln'texist", username()+"thisshoudln'tbehere");
    }

    /**
     * Tests that a custom policy properly causes a PolicyFailureException when the policy is not met.
     */
    @Test
    public void testCustomPolicy() throws ConnectionFailureException, UserAlreadyExistsException {
        UserDirectory ud = create();
        String user = username();
        String good = "foo";
        String bad = "";
        String goodUser = user+good;
        String badUser = user+bad;

        ud.setPolicy(new Policy() {
            @Override
            public boolean checkUsername(String username) {
                return !username.equals(badUser);
            }

            @Override
            public boolean checkEmail(String email) {
                return !email.equals(bad);
            }

            @Override
            public boolean checkScreenName(String screenName) {
                return !screenName.equals(bad);
            }

            @Override
            public boolean checkPassword(String password) {
                return !password.equals(bad);
            }
        });

        // test that valid user passes policy
        try {
            ud.addUser(goodUser, good, good, good);
            removeUserLater(goodUser);
        } catch (PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected PolicyFailureException");
        }
        assertTrue(ud.hasUser(goodUser));
        ud.removeUser(goodUser);
        assertFalse(ud.hasUser(goodUser));

        // test that invalid username fails policy
        try {
            ud.addUser(badUser, good, good, good);
            removeUserLater(badUser);
            // exception expected
            fail("Failed to throw a PolicyFailureException");
        } catch (PolicyFailureException e) {
            assertFalse(ud.hasUser(badUser));
            assertEquals(PolicyFailure.ILLEGAL_USERNAME, e.getFailure());
        }
        // test that invalid email fails policy
        try {
            ud.addUser(goodUser, bad, good, good);
            removeUserLater(goodUser);
            // exception expected
            fail("Failed to throw a PolicyFailureException");
        } catch (PolicyFailureException e) {
            assertFalse(ud.hasUser(goodUser));
            assertEquals(PolicyFailure.ILLEGAL_EMAIL, e.getFailure());
        }
        // test that invalid screen name fails policy
        try {
            ud.addUser(goodUser, good, bad, good);
            removeUserLater(goodUser);
            // exception expected
            fail("Failed to throw a PolicyFailureException");
        } catch (PolicyFailureException e) {
            assertFalse(ud.hasUser(goodUser));
            assertEquals(PolicyFailure.ILLEGAL_SCREEN_NAME, e.getFailure());
        }
        // test that invalid screen name fails policy
        try {
            ud.addUser(goodUser, good, good, bad);
            removeUserLater(goodUser);
            // exception expected
            fail("Failed to throw a PolicyFailureException");
        } catch (PolicyFailureException e) {
            assertFalse(ud.hasUser(good));
            assertEquals(PolicyFailure.ILLEGAL_PASSWORD, e.getFailure());
        }
    }

    /**
     * Tests the authenticateUserDetailed() method.
     */
    @Test
    public void testAuthenticateUserDetailed() throws ConnectionFailureException {
        UserDirectory ud = create();

        // create user
        String user = username();
        String pass = "pass";
        try {
            ud.addUser(user, "foo", "bar", pass);
            removeUserLater(user);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail("Unexpected Exception");
        }

        // test when user doesn't exist
        assertEquals(Authentication.INVALID_USERNAME, ud.authenticateUserDetailed(username()+"thisshoudn'texist", "pass"));

        // test when password is wrong
        assertEquals(Authentication.INVALID_PASSWORD, ud.authenticateUserDetailed(user, "wrong-pass"));

        // test when valid
        assertEquals(Authentication.VALID, ud.authenticateUserDetailed(user, pass));
    }

    private void removeUserLater(String username) {
        usersToRemove.add(username);
    }
}
