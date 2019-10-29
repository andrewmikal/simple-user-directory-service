package com.ajmi.simpleuserdirectoryservice.tests;

import com.ajmi.simpleuserdirectoryservice.user.PolicyFailureException;
import com.ajmi.simpleuserdirectoryservice.user.UserAlreadyExistsException;
import com.ajmi.simpleuserdirectoryservice.user.UserDirectory;
import com.ajmi.simpleuserdirectoryservice.user.UserDirectoryException;
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
            assertTrue(ud.addUser(uname, "foo", "bar", "baz"));
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
        String[] usernames = {username(), username(), username(), username(), username()};
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
}
