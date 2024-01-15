package com.insa.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.database.LocalDatabase;
import com.insa.network.connectedusers.ConnectedUserList;
import com.insa.utils.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

class UDPServerTest {

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private UDPServer server;

    @BeforeAll
    static void init() {
        ConnectedUserList.getInstance().clear();
    }

    @BeforeEach
    void setUp() {
        server = UDPServer.getInstance();
    }

    @AfterAll
    static void tearDown() {
        ConnectedUserList.getInstance().clear();
    }

    @Test
    void testGetInstance() {
        UDPServer anotherInstance = UDPServer.getInstance();
        assertSame(server, anotherInstance);
    }

    @Test
    void testRun() {
        server.start();
        assertTrue(server.isAlive());
    }


    @Test
    void testDiscovery() throws IOException {
        // Create Discovery message
        Message broadcastMsg = new Message();
        broadcastMsg.setType(Message.MessageType.DISCOVERY);
        User testDiscoveryUser = new User("testDiscovery");
        broadcastMsg.setSender(testDiscoveryUser);

        // Check that database is empty
        assertTrue(ConnectedUserList.getInstance().isEmpty());

        // Process msg
        server.dataProcessing(gson.toJson(broadcastMsg), InetAddress.getByName("255.255.255.255"), Constants.UDP_SERVER_PORT);

        // Assertions based on expected changes
        assertFalse(ConnectedUserList.getInstance().isEmpty());
        assertTrue(ConnectedUserList.getInstance().hasUsername(testDiscoveryUser.getUsername()));
    }

    @Test
    void testNewUser() throws IOException {
        // Create message
        Message message = new Message();
        message.setType(Message.MessageType.USER_CONNECTED);
        User testNewUser = new User("testNewUser");
        message.setSender(testNewUser);

        // Check that db only contains previous user
        assertEquals(1, ConnectedUserList.getInstance().size());

        server.dataProcessing(gson.toJson(message), InetAddress.getByName("255.255.255.255"), Constants.UDP_SERVER_PORT);

        // New user is added to the db
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(testNewUser.getUsername()));
    }
}