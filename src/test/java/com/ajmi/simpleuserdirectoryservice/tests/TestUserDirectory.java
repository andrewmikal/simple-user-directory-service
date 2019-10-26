package com.ajmi.simpleuserdirectoryservice.tests;

import com.ajmi.simpleuserdirectoryservice.user.UserDirectory;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

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
        assertTrue(ud.addUser(uname, "foo", "bar", "baz"));
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
        ud.addUser(uname, "foo", "bar", "baz");

        assertTrue(ud.hasUser(uname));
        assertTrue(ud.removeUser(uname));
        assertFalse(ud.hasUser(uname));
    }
}
