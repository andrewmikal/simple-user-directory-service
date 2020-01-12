package com.ajmi.simpleuserdirectoryservice.tests.data;

import com.ajmi.simpleuserdirectoryservice.data.UserData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for the UserData class.
 */
public class TestUserData {
    /**
     * Tests that the getter methods correctly return the values the UserData object was created with.
     */
    @Test
    public void testGetters() {
        String username = "foo";
        String email = "bar";
        String screenName = "baz";

        UserData data = new UserData(username, email, screenName);

        assertEquals(username, data.getUsername());
        assertEquals(email, data.getEmail());
        assertEquals(screenName, data.getScreenName());
    }

    /**
     * Tests that the equals() method correctly returns true when two UserData objects are create with the same data.
     */
    @Test
    public void testEqualsWhenEqual() {
        String username = "foo";
        String email = "bar";
        String screenName = "baz";

        UserData data1, data2;
        data1 = data2 = new UserData(username, email, screenName);

        assertEquals(data1, data2);
    }

    /**
     * Tests that thr equals() method correctly returns false when two UserData objects are created with different data.
     */
    @Test
    public void testEqualsWhenNotEqual() {
        UserData data1 = new UserData("foo", "bar", "baz");
        UserData data2 = new UserData("bar", "baz", "qux");

        assertNotEquals(data1, data2);
    }
}
