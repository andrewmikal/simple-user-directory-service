package com.ajmi.simpleuserdirectoryservice.user;

import sun.rmi.runtime.Log;

import javax.xml.transform.Result;
import java.sql.*;
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

    /**
     * Executes a SQL query to get the number of users in the database with the specified username, and returns true if
     * that value is equal to one.
     * @param username User name of the user to check for.
     * @return true if the number of users in the database with the specified username is equal to one.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public boolean hasUser(String username) throws ConnectionFailureException {
        final String USER_EXISTS = "SELECT COUNT(1) FROM users WHERE u_username=(?)";

        try (Connection connection = connect()) {
            try (PreparedStatement statement = connection.prepareStatement(USER_EXISTS)) {
                statement.setString(1, username);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                }
                return result.getInt(1)==1;
            }
        } catch (SQLException e) {
            String errorMsg = "Error checking if the user \""+username+"\" exists: ";
            LOGGER.log(Level.WARNING, errorMsg, e);
            throw new ConnectionFailureException(errorMsg, e);
        }
    }

    @Override
    public void addUser(String username, String email, String screeName, String password) throws ConnectionFailureException, UserAlreadyExistsException, PolicyFailureException {
        final String INSERT_USERS = "INSERT INTO users (u_email, u_username, u_screenname, u_salt) VALUES (?, ?, ?, ?)";
        final String INSERT_PASSWORDS = "INSERT INTO passwords (p_uid, p_hashed) VALUES (?, ?)";

        try (Connection connection = connect()) {
            // remember the original auto commit so it can be restored at the end of the function
            boolean originalAutoCommit = connection.getAutoCommit();
            // don't commit any table updates until all updates were successful
            connection.setAutoCommit(false);
            try {
                // salt stored in users, used for encrypting password in passwords
                String salt = PasswordCrypt.nextSalt();
                // user id created when the user is added to the users table
                int uID;
                // insert into users table
                try (PreparedStatement statement = connection.prepareStatement(INSERT_USERS, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, email);
                    statement.setString(2, username);
                    statement.setString(3, screeName);
                    statement.setString(4, salt);
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Error getting keys from insert users statement.");
                        }
                        uID = keys.getInt(1);
                    }
                }
                // insert into passwords table
                try (PreparedStatement statement = connection.prepareStatement(INSERT_PASSWORDS)) {
                    statement.setInt(1, uID);
                    statement.setString(2, PasswordCrypt.hashPassword(password, salt));
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                }
                // update tables
                connection.commit();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error adding user \""+username+"\": ", e);
                // revert changes
                connection.rollback();
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            // error connecting
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
        }
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
        final String PASSWORDS_ADD_CONSTRAINT = "ALTER TABLE passwords ADD CONSTRAINT passwords_p_uid_fkey FOREIGN KEY (p_uid) REFERENCES users (u_id) ON DELETE CASCADE;";
        try (Connection connection = connect()) {
            // remember the original auto commit so it can be restored at the end of the function
            boolean originalAutoCommit = connection.getAutoCommit();
            // don't commit any table updates until all updates were successful
            connection.setAutoCommit(false);
            try {
                // create the users table
                try (PreparedStatement statement = connection.prepareStatement(CREATE_USERS_TABLE)) {
                    statement.execute();
                }
                // create the passwords table
                try (PreparedStatement statement = connection.prepareStatement(CREATE_PASSWORDS_TABLE)) {
                    statement.execute();
                }
                // add constraints to passwords table
                try (PreparedStatement statement = connection.prepareStatement(PASSWORDS_ADD_CONSTRAINT)) {
                    statement.execute();
                }
                // add tables
                connection.commit();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error creating database tables: ", e);
                // revert changes
                connection.rollback();
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
            throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
        }
    }

    private boolean tableExists() {
        return false;
    }
}
