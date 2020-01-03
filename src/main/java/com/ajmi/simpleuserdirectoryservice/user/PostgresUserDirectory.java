package com.ajmi.simpleuserdirectoryservice.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    /** String logged when SQL execution fails. */
    private static final String SQL_EXEC_FAILURE_MSG = "Error executing SQL statement: ";

    /** String logged when SQL connection fails. */
    private static final String CONNECTION_FAILURE_MSG = "Failed to connect to Postgres database: ";

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
    public PostgresUserDirectory(String host, String database, String user, String pass) throws ConnectionFailureException {
        _postgresURL = String.format("jdbc:postgresql://%s/%s", host, database);
        _postgresUser = user;
        _postgresPass = pass;
        createTables();
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
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
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

    /**
     * Creates the users and passwords tables in the database if they don't already exist.
     */
    private void createTables() throws ConnectionFailureException{
        final String CREATE_USERS_TABLE = "CREATE TABLE users (u_id SERIAL PRIMARY KEY, u_email TEXT, u_username TEXT NOT NULL UNIQUE, u_screenname TEXT NOT NULL, u_salt TEXT NOT NULL);";
        final String CREATE_PASSWORDS_TABLE = "CREATE TABLE passwords (p_uid INTEGER PRIMARY KEY, p_hashed CHAR(128) NOT NULL);";
        final String PASSWORDS_DROP_CONSTRAINT = "alter table passwords drop constraint passwords_p_uid_fkey;";
        final String PASSWORDS_ADD_CONSTRAINT = "alter table passwords add constraint passwords_p_uid_fkey foreign key (p_uid) references users (u_id) on delete cascade;";
        try (Connection connection = connect()) {
            // remember the original auto commit so it can be restored at the end of the function
            boolean originalAutoCommit = connection.getAutoCommit();
            // don't commit any table updates until all updates were successful
            connection.setAutoCommit(false);
            try {
                // create the users table
                try (PreparedStatement statement = connection.prepareStatement(CREATE_USERS_TABLE)) {
                    // execute the SQL statement and if it fails to execute throw an exception
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                }
                // create the passwords table
                try (PreparedStatement statement = connection.prepareStatement(CREATE_PASSWORDS_TABLE)) {
                    // execute the SQL statement and if it fails to execute throw an exception
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                }
                // drop constraints from passwords table
                try (PreparedStatement statement = connection.prepareStatement(PASSWORDS_DROP_CONSTRAINT)) {
                    // execute the SQL statement and if it fails to execute throw an exception
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                }
                // add constraints to passwords table
                try (PreparedStatement statement = connection.prepareStatement(PASSWORDS_ADD_CONSTRAINT)) {
                    // execute the SQL statement and if it fails to execute throw an exception
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error creating database tables: ", e);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
            throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
        }
    }
}
