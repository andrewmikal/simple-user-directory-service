package com.ajmi.simpleuserdirectoryservice.tests.directory;

import com.ajmi.simpleuserdirectoryservice.directory.ConnectionFailureException;
import com.ajmi.simpleuserdirectoryservice.directory.PostgresUserDirectory;
import com.ajmi.simpleuserdirectoryservice.directory.UserDirectory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;

/**
 * Tests for the PostgresUserDirectory class.
 */
public class TestPostgresUserDirectory extends TestUserDirectory {

    /**
     * Creates a new PostgresUserDirectory instance.
     * @return Returns a new UserDirectory.
     */
    @Override
    protected UserDirectory create() {
        return createPostgres();
    }

    /**
     * Tests the connection to the PostgresUserDirectory.
     */
    @Test
    public void testConnection() {
        assertTrue(createPostgres().testConnection());
    }

    /**
     * Creates a new PostgresUserDirectory instance. Requires the 'suds.pg.host', 'suds.pg.database', 'suds.pg.user',
     * 'suds.pg.pass' to be set in a suds-test.properties file, indicating the Postgres URL, Postgres database, Postgres
     * username, and Postgres password respectively.
     * @return Returns a new PostgresUserDirectory.
     */
    private PostgresUserDirectory createPostgres() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("suds-test.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not find file \"suds-test.properties\": ", e);
        }
        try {
            return new PostgresUserDirectory(properties.getProperty("suds.pg.host"),
                    properties.getProperty("suds.pg.database"),
                    properties.getProperty("suds.pg.user"),
                    properties.getProperty("suds.pg.pass"));
        } catch (ConnectionFailureException e) {
            throw new RuntimeException(e);
        }
    }
}
