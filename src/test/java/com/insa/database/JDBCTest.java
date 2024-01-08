package com.insa.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class JDBCTest {

    private JDBC db = null;

    @Test
    void getDBConnection() {
        assertNotNull(JDBC.getDBConnection());
    }
}

/*
    @Test
    void createTables() throws SQLException, InterruptedException {
        // Test tables creations
        Connection con = JDBC.getDBConnection();
        JDBC.createTables(con);

        assertNotNull(con.getMetaData().getTables(null, null, "connected_users", null));
        assertNotNull(con.getMetaData().getTables(null, null, "message_history", null));


        Thread.sleep(100);
        // Delete tables
        Statement stmt = con.createStatement();
        String deleteTables = "DROP TABLE connected_users; DROP TABLE message_history;";

        stmt.executeUpdate(deleteTables);
        stmt.close();
    }

 */