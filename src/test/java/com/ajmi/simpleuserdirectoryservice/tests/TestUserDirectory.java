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
public class TestUserDirectory {

    /** Used to format current time when creating new entries in the UserDirectory. */
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.FULL)
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    /**
     * Creates a new user directory.
     * @return a new UserDirectory object.
     */
    private static UserDirectory create() {
        return new UserDirectory() {
            public boolean has(String username) {
                return false;
            }
        };
    }

    /**
     * Checks that has() returns true when the specified user does not exist in the directory.
     */
    @Test
    public void testHasDNE() {
        UserDirectory ud = create();
        // check if the directory has a user that doesn't exist
        assertFalse(ud.has(formatter.format(Instant.now())));
    }
}
