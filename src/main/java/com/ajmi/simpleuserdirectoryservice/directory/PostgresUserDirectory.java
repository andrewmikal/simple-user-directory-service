package com.ajmi.simpleuserdirectoryservice.directory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ajmi.simpleuserdirectoryservice.cryptography.PasswordCrypt;
import com.ajmi.simpleuserdirectoryservice.data.Authentication;
import com.ajmi.simpleuserdirectoryservice.data.UserData;
import com.ajmi.simpleuserdirectoryservice.data.PolicyFailure;
import javafx.util.Pair;

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

    /** SQL statement for adding a new user to the users table in the database. */
    private static final String INSERT_USERS = "INSERT INTO users (u_email, u_username, u_screenname, u_salt) VALUES (?, ?, ?, ?)";
    /** SQL statement for adding a new user to the passwords table in the database. */
    private static final String INSERT_PASSWORDS = "INSERT INTO passwords (p_uid, p_hashed) VALUES (?, ?)";
    /** SQL statement for removing a user from the database. */
    private static final String REMOVE_USERS= "DELETE FROM users WHERE u_username=(?)";
    /** SQL statement for retrieving the usernames of all users in the database. */
    private static final String GET_USERS = "SELECT u_username FROM users";
    /** SQL statement for retrieving the password for a specific user in the database. */
    private static final String GET_HASHED = "SELECT p_hashed FROM passwords WHERE p_uid=(?)";
    /** SQL statement for retrieving the email and screen name for a specific user in the database. */
    private static final String GET_DATA = "SELECT u_email, u_screenname FROM users WHERE u_username=(?)";
    /** SQL statement for changing a specific user's username. */
    private static final String UPDATE_USERNAME = "UPDATE users SET u_username=(?) WHERE u_username=(?)";
    /** SQL statement for changing a specific user's email. */
    private static final String UPDATE_EMAIL = "UPDATE users SET u_email=(?) WHERE u_username=(?)";
    /** SQL statement for changing a specific user's screen name. */
    private static final String UPDATE_SCREENNAME = "UPDATE users SET u_screenname=(?) WHERE u_username=(?)";
    /** SQL statement for changing a specific user's password. */
    private static final String UPDATE_PASSWORD = "UPDATE passwords SET p_hashed=(?) WHERE p_uid=(?)";
    /** SQL statement to create the 'users' table in the database. */
    private static final String CREATE_USERS_TABLE = "CREATE TABLE users (u_id SERIAL PRIMARY KEY, u_email TEXT, u_username TEXT NOT NULL UNIQUE, u_screenname TEXT NOT NULL, u_salt TEXT NOT NULL);";
    /** SQL statement to create the `passwords` table in the database. */
    private static final String CREATE_PASSWORDS_TABLE = "CREATE TABLE passwords (p_uid INTEGER PRIMARY KEY, p_hashed CHAR(128) NOT NULL);";
    /** SQL statement to add a cascading delete to the passwords table. */
    private static final String PASSWORDS_ADD_CONSTRAINT = "ALTER TABLE passwords ADD CONSTRAINT passwords_p_uid_fkey FOREIGN KEY (p_uid) REFERENCES users (u_id) ON DELETE CASCADE;";
    /** SQL statement to fetch the id anf salt of a specific user. */
    private static final String GET_ID_AND_SALT = "SELECT u_id, u_salt FROM users WHERE u_username=(?)";

    /** URL to the postgres database. */
    private final String _postgresURL;
    /** Username to log into the postgres database. */
    private final String _postgresUser;
    /** Password to log into the postgres database. */
    private final String _postgresPass;

    /** Policy used do tetermine the vadility of usernames, emails, screen names, and passwords. */
    private Policy _policy;

    /**
     * Create a new PostgresUserDirectory with the credentials to log into the PostreSQL database.
     * @param host the url to the Postgres instance.
     * @param database the name of the Postgres database.
     * @param user the username needed to log into the Postgres instance.
     * @param pass the password needed to log into Postgres instance.
     */
    public PostgresUserDirectory(String host, String database, String user, String pass) throws ConnectionFailureException {
        _postgresURL = String.format("jdbc:postgresql://%s/%s", host, database);
        _postgresUser = user;
        _postgresPass = pass;
        _policy = new Policy() {
            @Override
            public boolean checkUsername(String username) {
                return true;
            }

            @Override
            public boolean checkEmail(String email) {
                return true;
            }

            @Override
            public boolean checkScreenName(String screenName) {
                return true;
            }

            @Override
            public boolean checkPassword(String password) {
                return true;
            }
        };
        // create tables if they don't already exist
        if (!tableExists("users")) {
            createTables();
        }
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
     * @param username the user name of the user to check for.
     * @return true if the number of users in the database with the specified username is equal to one.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public boolean hasUser(String username) throws ConnectionFailureException {
        final String USER_EXISTS = "SELECT COUNT(1) FROM users WHERE u_username=(?)";
        // boolean to return
        boolean hasUser;
        try (Connection connection = connect()) {
            try (PreparedStatement statement = connection.prepareStatement(USER_EXISTS)) {
                statement.setString(1, username);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                    hasUser = result.getInt(1) == 1;
                }
            }
        } catch (SQLException e) {
            String errorMsg = "Error checking if the user \""+username+"\" exists: ";
            LOGGER.log(Level.WARNING, errorMsg, e);
            throw new ConnectionFailureException(errorMsg, e);
        }
        return hasUser;
    }

    /**
     * Executes a SQL update to insert a new user into the users and passwords tables.
     * @param username the user name of the new entry.
     * @param email the email of the new entry.
     * @param screeName the screen name of the new entry.
     * @param password the password of the new entry.
     * @throws ConnectionFailureException if a SQLException occurs.
     * @throws UserAlreadyExistsException if the user directory already has a user with the specified name.
     * @throws PolicyFailureException if the username, email, screen name, or password fail the directory's policy.
     */
    @Override
    public void addUser(String username, String email, String screeName, String password) throws ConnectionFailureException, UserAlreadyExistsException, PolicyFailureException {
        // make sure the user doesn't already exist
        if (hasUser(username)) {
            throw new UserAlreadyExistsException("User \"" + username + "\" already exists in the database.");
        }
        // make sure the username, email, screen name, or password pass the user directory's policy
        if (!_policy.checkUsername(username)) {
            throw new PolicyFailureException("Username policy failure.", PolicyFailure.ILLEGAL_USERNAME);
        }
        if (!_policy.checkEmail(email)) {
            throw new PolicyFailureException("Email policy failure.", PolicyFailure.ILLEGAL_EMAIL);
        }
        if (!_policy.checkScreenName(screeName)) {
            throw new PolicyFailureException("Screen name policy failure.", PolicyFailure.ILLEGAL_SCREEN_NAME);
        }
        if (!_policy.checkPassword(password)) {
            throw new PolicyFailureException("Password policy failure.", PolicyFailure.ILLEGAL_PASSWORD);
        }

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
            throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
        }
    }

    /**
     *
     * @param username the user name of the user to remove.
     * @return true if the user exists in the directory and the user was removed from the directory, false if the
     * directory does not have the specified user.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public boolean removeUser(String username) throws ConnectionFailureException {
        // boolean to return
        boolean userRemoved = false;
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                // remember the original auto commit so it can be restored at the end of the function
                boolean originalAutoCommit = connection.getAutoCommit();
                // don't commit any table updates until all updates were successful
                connection.setAutoCommit(false);
                try (PreparedStatement statement = connection.prepareStatement(REMOVE_USERS)) {
                    statement.setString(1, username);
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException(SQL_EXEC_FAILURE_MSG + statement.toString());
                    }
                    // update tables
                    connection.commit();
                    // return true since the user was successfully removed
                    userRemoved = true;
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error removing user \""+username+"\": ", e);
                    // revert changes
                    connection.rollback();
                } finally {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        }
        return userRemoved;
    }

    /**
     * Executes a SQL query to retrieve the usernames of all users in the directory.
     * @return a String[] containing the usernames of all users in the directory.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public String[] getUsers() throws ConnectionFailureException {
        // collection of usernames to be converted to an array and returned
        ArrayList<String> usernames = new ArrayList<>();
        try (Connection connection = connect()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_USERS)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        usernames.add(result.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            // error connecting
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
            throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
        }
        // convert string array list into string array and return it
        String[] usernamesArr = new String[usernames.size()];
        usernamesArr = usernames.toArray(usernamesArr);
        return usernamesArr;
    }

    /**
     * Returns a reference to the directory's Policy field.
     * @return a reference to the directory's Policy field.
     */
    @Override
    public Policy getPolicy() {
        return _policy;
    }

    /**
     * Sets the directory's internal policy field.
     * @param policy the Policy to set the directory's policy to.
     */
    @Override
    public void setPolicy(Policy policy) {
        _policy = policy;
    }

    /**
     * Checks that the given password matches the hashed password in the database when hashed with the same salt.
     * @param username the username of user to authenticate.
     * @param password the password used to authenticate the user.
     * @return true if the hashed passwords match, false if they don't or if the user does not exist in the directory.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public boolean authenticateUser(String username, String password) throws ConnectionFailureException {
        return authenticateUserDetailed(username, password) == Authentication.VALID;
    }

    /**
     * Checks that the given password matches the hashed password in the database when hashed with the same salt.
     * @param username of user to authenticate.
     * @param password used to authenticate the user.
     * @return INVALID_USERNAME if the user doesn't exist in the directory, INVALID_PASSWORD if the passwords don't
     * match, and VALID if the passwords do match.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public Authentication authenticateUserDetailed(String username, String password) throws ConnectionFailureException {
        // to return
        Authentication authentication;
        // if the user doesn't exist then the authentication fails
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                // hashed password found for the specified user in the database
                String hashed;
                // get user ID and salt
                Pair<Integer, String> idAndSalt = fetchIDAndSalt(username);
                // ID for the specified user in the database
                int id = idAndSalt.getKey();
                // salt used to hash the specified user's password
                String salt = idAndSalt.getValue();
                // get hashed password
                try (PreparedStatement statement = connection.prepareStatement(GET_HASHED)) {
                    statement.setInt(1, id);
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        hashed = result.getString(1);
                    }
                }
                if (PasswordCrypt.hashPassword(password, salt).equals(hashed)) {
                    authentication = Authentication.VALID;
                } else {
                    authentication = Authentication.INVALID_PASSWORD;
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        } else {
            authentication = Authentication.INVALID_USERNAME;
        }
        return authentication;
    }

    /**
     * Executes a SQL query to retrieve the email and screen name of the specified user.
     * @param username the username of the user to retrieve data on.
     * @return a new UserData object from the result of the SQL query if the user exists, and null if the user does not
     * exist in the directory.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public Optional<UserData> getUserData(String username) throws ConnectionFailureException {
        // to return
        Optional<UserData> data;
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                try (PreparedStatement statement = connection.prepareStatement(GET_DATA)) {
                    statement.setString(1, username);
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        data = Optional.of(new UserData(username, result.getString(1), result.getString(2)));
                    }
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        } else {
            data = Optional.empty();
        }
        return data;
    }

    /**
     * Updates the specified user in the database with a new username.
     * @param username the username of the user to update.
     * @param newUsername the username to change the user's current username to.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public void updateUsername(String username, String newUsername) throws ConnectionFailureException {
        updateValue(username, newUsername, UPDATE_USERNAME);
    }

    /**
     * Updates the specified user in the database with a new email.
     * @param username the username of the user to update.
     * @param newEmail the email to change the user's current email to.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public void updateEmail(String username, String newEmail) throws ConnectionFailureException {
        updateValue(username, newEmail, UPDATE_EMAIL);
    }

    /**
     * Updates the specified user in the database with a new screen name.
     * @param username the username of the user to update.
     * @param newScreenName the screen name to change the user's current screen name to.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public void updateScreenName(String username, String newScreenName) throws ConnectionFailureException {
        updateValue(username, newScreenName, UPDATE_SCREENNAME);
    }

    /**
     * Updates the specified user in the database with a new password.
     * @param username the username of the user to update.
     * @param newPassword the password to change the user's current password to.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    @Override
    public void updatePassword(String username, String newPassword) throws ConnectionFailureException {
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                // get user ID and salt
                Pair<Integer, String> idAndSalt = fetchIDAndSalt(username);
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_PASSWORD)) {
                    statement.setString(1, PasswordCrypt.hashPassword(newPassword, idAndSalt.getValue()));
                    statement.setInt(2, idAndSalt.getKey());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        }
    }

    /**
     * Creates a new connection to the directory's database.
     * @return a new SQL Connection object to the directory's database.
     * @throws SQLException Thrown when their is a problem connecting to the database.
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(_postgresURL, _postgresUser, _postgresPass);
    }

    /**
     * Creates the users and passwords tables in the database if they don't already exist.
     */
    private void createTables() throws ConnectionFailureException{
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

    /**
     * Checks if a table with the specified name exists in the database.
     * @param tableName the name of the table to check for.
     * @return true if the table exists, false otherwise
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    private boolean tableExists(String tableName) throws ConnectionFailureException {
        // boolean to return
        boolean tableExists;
        try (Connection connection = connect()) {
            try (ResultSet result = connection.getMetaData().getTables(null, null, tableName, null)) {
                // return true if the table exists, false if it doesn't
                tableExists = result.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
            throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
        }
        return tableExists;
    }

    /**
     * Executes the given SQL statement to update a value for the specified user.
     * @param username the username of the user to update.
     * @param newValue the new value to set the current value to.
     * @param sql the SQL statement to execute.
     * @throws ConnectionFailureException if a SQLException occurs.
     */
    private void updateValue(String username, String newValue, String sql) throws ConnectionFailureException {
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, newValue);
                    statement.setString(2, username);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        }
    }

    /**
     * Executes a SQL query to fetch the id and salt for a specified user.
     * @param username the username of the user to fetch the id and salt for.
     * @return the user's id and salt if the user exists in the directory, null otherwise.
     * @throws ConnectionFailureException if s SQLException occurs.
     */
    private Pair<Integer, String> fetchIDAndSalt(String username) throws ConnectionFailureException {
        // pair of ID and salt to return
        Pair<Integer, String> idAndSalt = null;
        // if the directory doesn't have the specified user, return null
        if (hasUser(username)) {
            try (Connection connection = connect()) {
                try (PreparedStatement statement = connection.prepareStatement(GET_ID_AND_SALT)) {
                    statement.setString(1, username);
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        idAndSalt = new Pair<>(result.getInt(1), result.getString(2));
                    }
                }
            } catch (SQLException e) {
                // error connecting
                LOGGER.log(Level.WARNING, CONNECTION_FAILURE_MSG, e);
                throw new ConnectionFailureException(CONNECTION_FAILURE_MSG, e);
            }
        }
        return idAndSalt;
    }
}
