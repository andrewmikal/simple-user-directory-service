package com.ajmi.simpleuserdirectoryservice.tests;

import com.ajmi.simpleuserdirectoryservice.user.EphemeralUserDirectory;
import com.ajmi.simpleuserdirectoryservice.user.UserDirectory;

/**
 * Tests for the EphemeralUserDirectory class.
 */
public class TestEphemeralUserDirectory extends TestUserDirectory {
    /**
     * Creates a new EphemeralUserDirectory instance.
     * @return Returns a new EphemeralUserDirectory.
     */
    @Override
    protected UserDirectory create() {
        return new EphemeralUserDirectory();
    }
}
