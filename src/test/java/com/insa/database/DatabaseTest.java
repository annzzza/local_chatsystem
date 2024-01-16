package com.insa.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void getDBConnection() {
        assertNotNull(Database.getDBConnection());
    }
}