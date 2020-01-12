package com.ajmi.simpleuserdirectoryservice.tests.directory;

import com.ajmi.simpleuserdirectoryservice.directory.EphemeralUserDirectory;
import com.ajmi.simpleuserdirectoryservice.directory.UserDirectory;

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
