package com.ajmi.simpleuserdirectoryservice.tests;

import com.ajmi.simpleuserdirectoryservice.user.*;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static junit.framework.TestCase.*;

/**
 * Tests for the UserDirectory interface.
 */
public abstract class TestUserDirectory {

    /** Used to format current time when creating new entries in the UserDirectory. */
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.FULL)
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    /**
     * Creates a new user directory.
     * @return a new UserDirectory object.
     */
    protected abstract UserDirectory create();

    /**
     * Creates a username created from the current time.
     * @return a String to use as a username based on the current time.
     */
    private static String username() {
        return "TestUserDirectory-Username:"+formatter.format(Instant.now());
    }

    /**
     * Tests the hasUser() method when the user does not exist in the directory.
     */
    @Test
    public void testHasUserDNE() {
        UserDirectory ud = create();
        // check if the directory hasUser a user that doesn't exist
        assertFalse(ud.hasUser(username()));
    }

    /**
     * Tests the addUser() method when the user does not already exist in the directory and the hasUser() method when
     *  the user is added to the directory.
     */
    @Test
    public void testAddUserHasUser() {
        UserDirectory ud = create();
        String uname = username();

        assertFalse(ud.hasUser(uname));
        try {
            ud.addUser(uname, "foo", "bar", "baz");
        } catch (UserAlreadyExistsException e) {
            // expected exception
        } catch (UserDirectoryException e) {
            // unexpected exception
            fail();
        }
        assertTrue(ud.hasUser(uname));
    }

    /**
     * Tests the removeUser() method when the user does not exist in the directory.
     */
    @Test
    public void testRemoveUserDNE() {
        UserDirectory ud = create();
        assertFalse(ud.removeUser(username()));
    }

    /**
     * Tests the removeUser() method when the user exists in the directory.
     */
    @Test
    public void testRemoveUserExists() {
        UserDirectory ud = create();
        String uname = username();
        try {
            ud.addUser(uname, "foo", "bar", "baz");
        } catch (UserDirectoryException e) {
            // unexpected exception
            fail();
        }

        assertTrue(ud.hasUser(uname));
        assertTrue(ud.removeUser(uname));
        assertFalse(ud.hasUser(uname));
    }

    /**
     * Tests the getUsers() method.
     */
    @Test
    public void testGetUsers() {
        UserDirectory ud = create();
        String[] usernames = {"1"+username(), "2"+username(), "3"+username(), "4"+username(), "5"+username()};
        for (String name : usernames) {
            try {
                ud.addUser(name, "foo", "bar", "baz");
            } catch (UserDirectoryException e) {
                // unexpected exception
                fail();
            }
        }
        assertEquals(usernames, ud.getUsers());
    }

    /**
     * Tests that the getPolicy() method does not return null.
     */
    @Test
    public void testGetPolicyNotNull() {
        UserDirectory ud = create();
        assertNotNull(ud.getPolicy());
    }

    /**
     * Test authenticateUser().
     */
    @Test
    public void testAuthenticateUser() {
        UserDirectory ud = create();

        // create user
        String user = username();
        String pass = "password"+username();
        try {
            ud.addUser(user, "foo", "bar", pass);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
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
    public void testGetUserData() {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";

        try {
            ud.addUser(user, email, screen, "baz");
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
        }

        assertEquals(ud.getUserData(user), new UserData(user, email, screen));
    }

    /**
     * Tests updateUsername().
     */
    @Test
    public void testUpdateUsername() {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newUser = username();

        try {
            ud.addUser(user, email, screen, pass);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
        }
        assertTrue(ud.hasUser(user));

        ud.updateUsername(user, newUser);
        assertFalse(ud.hasUser(user));
        assertTrue(ud.hasUser(newUser));

        assertEquals(ud.getUserData(newUser), new UserData(newUser, email, screen));
        assertTrue(ud.authenticateUser(newUser, pass));
    }

    /**
     * Tests updateEmail().
     */
    @Test
    public void testUpdateEmail() {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newEmail = "qux";

        try {
            ud.addUser(user, email, screen, pass);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
        }
        ud.updateEmail(user, newEmail);

        assertEquals(ud.getUserData(user), new UserData(user, newEmail, screen));
        assertTrue(ud.authenticateUser(user, pass));
    }

    /**
     * Tests updateScreenName().
     */
    @Test
    public void testUpdateScreenName() {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newScreen = "qux";

        try {
            ud.addUser(user, email, screen, pass);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
        }
        ud.updateScreenName(user, newScreen);

        assertEquals(ud.getUserData(user), new UserData(user, email, newScreen));
        assertTrue(ud.authenticateUser(user, pass));
    }

    /**
     * Tests updatePassword().
     */
    @Test
    public void testUpdatePassword() {
        UserDirectory ud = create();

        String user = username();
        String email = "foo";
        String screen = "bar";
        String pass = "baz";
        String newPass = "qux";

        try {
            ud.addUser(user, email, screen, pass);
        } catch (UserAlreadyExistsException | PolicyFailureException e) {
            // unexpected exception
            fail();
        }
        ud.updatePassword(user, newPass);

        assertEquals(ud.getUserData(user), new UserData(user, email, screen));
        assertTrue(ud.authenticateUser(user, newPass));
    }
}
