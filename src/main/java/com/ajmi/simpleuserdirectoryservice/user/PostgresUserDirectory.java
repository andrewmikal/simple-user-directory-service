package com.ajmi.simpleuserdirectoryservice.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User Directory using a PostgreSQL Database.
 */
public class PostgresUserDirectory implements UserDirectory {

    /*
    Register JDBC drivers for PostgreSQL.
     */
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to find PostgreSQL JDBC driver: ", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PostgresUserDirectory.class.getName());

    /** URL to the postgres database. */
    private final String _postgresURL;
    /** Username to log into the postgres database. */
    private final String _postgresUser;
    /** Password to log into the postgres database. */
    private final String _postgresPass;

    /**
     * Create a new PostgresUserDirectory with the credentials to log into the PostreSQL database.
     * @param host URL to the Postgres instance.
     * @param database Name of the Postgres database.
     * @param user Username to log into Postgres.
     * @param pass Password to log into Postgres.
     */
    public PostgresUserDirectory(String host, String database, String user, String pass) {
        _postgresURL = String.format("jdbc:postgresql://%s/%s", host, database);
        _postgresUser = user;
        _postgresPass = pass;
    }

    /**
     * Attempts to connect to the Postgres database to test the connection.
     * @return true if the connection was successful, false if it could not connect.
     */
    public boolean testConnection() {
        boolean connected;
        try (Connection connection = connect()) {
            connected = true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to connect to Postgres database: ", e);
            connected = false;
        }
        return connected;
    }

    @Override
    public boolean hasUser(String username) throws ConnectionFailureException {
        return false;
    }

    @Override
    public void addUser(String username, String email, String screeName, String password) throws ConnectionFailureException, UserAlreadyExistsException, PolicyFailureException {

    }

    @Override
    public boolean removeUser(String username) throws ConnectionFailureException {
        return false;
    }

    @Override
    public String[] getUsers() throws ConnectionFailureException {
        return new String[0];
    }

    @Override
    public Policy getPolicy() throws ConnectionFailureException {
        return null;
    }

    @Override
    public boolean authenticateUser(String username, String password) throws ConnectionFailureException {
        return false;
    }

    @Override
    public UserData getUserData(String username) throws ConnectionFailureException {
        return null;
    }

    @Override
    public void updateUsername(String username, String newUsername) throws ConnectionFailureException {

    }

    @Override
    public void updateEmail(String username, String newEmail) throws ConnectionFailureException {

    }

    @Override
    public void updateScreenName(String username, String newScreenName) throws ConnectionFailureException {

    }

    @Override
    public void updatePassword(String username, String newPassword) throws ConnectionFailureException {

    }

    /**
     * Creates a new connection to the directory's database.
     * @return Returns a new SQL Connection object to the directory's database.
     * @throws SQLException Thrown when their is a problem connecting to the database.
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(_postgresURL, _postgresUser, _postgresPass);
    }
}
