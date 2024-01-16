package com.insa.database;

import com.insa.utils.MyLogger;

import java.sql.*;

public class Database {

    private static final MyLogger LOGGER = new MyLogger(Database.class.getName());

    private static final String DB_DRIVER = "org.sqlite.JDBC";

    public synchronized static Connection getDBConnection() {
        Connection con = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            con = DriverManager.getConnection("jdbc:sqlite:history.db");

        } catch (SQLException e) {
            LOGGER.severe("Connection to database failed.");
        }
        LOGGER.info("Connection to database successful.");
        return con;
    }

    public static void createTables(Connection con) throws SQLException {
        Statement stmt = con.createStatement();

        LOGGER.info("Creating tables in database");
        // first table: history of messages
        String tableHistory = "CREATE TABLE message_history "
                + "(uuid CHAR(36) PRIMARY KEY NOT NULL, "
                + "content CHAR(280) NOT NULL, "
                + "date DATETIME, "
                + "sender_username CHAR(200) NOT NULL,"
                + "receiver_username CHAR(200) NOT NULL);";

        stmt.executeUpdate(tableHistory);
        stmt.close();
    }
}
