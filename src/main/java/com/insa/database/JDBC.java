package com.insa.database;

import com.insa.utils.MyLogger;

import java.sql.*;

public class JDBC {

    private static final String DB_DRIVER = "org.sqlite.JDBC";

    public synchronized static Connection getDBConnection(){
        Connection con;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            con = DriverManager.getConnection("jdbc:sqlite:test.db");



        } catch (SQLException e) {
            System.out.println("SQLException, Class Database, getDBConnection()" + e);
            throw new RuntimeException(e);
        }
        MyLogger.getInstance().info("Connection to database successful.");
        return con;
    }

    public static void createTables(Connection con) throws SQLException {

        Statement stmt = con.createStatement();

        //first table : connected users
        String tableConnectedUsersSQL = "CREATE TABLE connected_users "
                + "(uuid CHAR(36) PRIMARY KEY NOT NULL, "
                + "username CHAR(200) NOT NULL, "
                + "ip CHAR(15) NOT NULL);";

        //second table: history of messages
        String tableHistory = "CREATE TABLE message_history "
                + "(uuid CHAR(36) PRIMARY KEY NOT NULL, "
                + "content CHAR(280) NOT NULL, "
                + "date DATETIME, "
                + "sender_username CHAR(200) NOT NULL,"
                + "receiver_username CHAR(200) NOT NULL);";

        stmt.executeUpdate(tableConnectedUsersSQL);
        stmt.executeUpdate(tableHistory);
        stmt.close();
    }
}
