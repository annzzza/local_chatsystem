package com.insa.database;

import com.insa.utils.MyLogger;

import java.sql.*;


/**
 * Database class
 * It is used to create the database and to get the connection to the database
 * It is a singleton to ensure that only one connection is created as it is a costly operation and to avoid concurrency issues
 */
public class Database {

    private static final MyLogger LOGGER = new MyLogger(Database.class.getName());

    /**
     * Database driver name
     * It is used to get the connection to the database
     * Here we use SQLite to store the history of the messages on each client
     */
    private static final String DB_DRIVER = "org.sqlite.JDBC";

    /**
     * Singleton instance
     */
    private static volatile Database instance;

    /**
     * Connection to the database (singleton)
     */
    private static Connection con;

    /**
     * Method to get the singleton instance of the database (lazy instantiation)
     * @return the singleton instance of the database
     */
    public synchronized static Database getInstance() {
        Database result = instance;
        if (result != null) {
            return result;
        }
        synchronized (Database.class) {
            if (instance == null) {
                instance = new Database();
                con = getDBConnection();
            }
            return instance;
        }
    }

    /**
     * Method to get the connection to the database (lazy instantiation)
     * @return the connection to the database
     */
    public synchronized Connection getConnection() {
        if (con == null) {
            con = getDBConnection();
        }
        return con;
    }

    /**
     * Method to create the connection to the database
     * @return the connection to the database
     */
    private synchronized static Connection getDBConnection() {
        Connection con = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Database driver was not found: " + e);
        }
        try {
            String dbpath = "history.db";
//            String dbpathtest = "/tmp/history.db";

            con = DriverManager.getConnection("jdbc:sqlite:" + dbpath);

        } catch (SQLException e) {
            LOGGER.severe("Connection to database failed.");
        }
        LOGGER.info("Connection to database successful.");
        return con;
    }

    /**
     * Method to create the tables in the database
     * @param con the connection to the database
     * @throws SQLException if the tables could not be created
     */
    public static void createTables(Connection con) throws SQLException {
        Statement stmt = con.createStatement();

        LOGGER.info("Creating tables in database");
        // first table: history of messages
        String tableHistory = "CREATE TABLE message_history "
                + "(uuid VARCHAR(36) PRIMARY KEY NOT NULL, "
                + "content TEXT NOT NULL, "
                + "date DATETIME, "
                + "sender_username VARCHAR(200) NOT NULL,"
                + "receiver_username VARCHAR(200) NOT NULL);";

        stmt.executeUpdate(tableHistory);
        stmt.close();
    }
}
