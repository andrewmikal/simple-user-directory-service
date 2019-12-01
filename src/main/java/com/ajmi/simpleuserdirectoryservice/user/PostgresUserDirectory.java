package com.ajmi.simpleuserdirectoryservice.user;

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

    /** URL to the postgres database. */
    private final String _postgresURL;
    /** Username to log into the postgres database. */
    private final String _postgresUser;
    /** Password to log into the postgres database. */
    private final String _postgresPass;

    /**
     * Create a new PostgresUserDirectory with the credentials to log into the PostreSQL database.
     * @param url URL to the database.
     * @param user Username to log into the database.
     * @param pass Password to log into the database.
     */
    public PostgresUserDirectory(String url, String user, String pass) {
        _postgresURL = url;
        _postgresUser = user;
        _postgresPass = pass;
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
}
