package com.insa.network.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.users.User;
import com.insa.users.ConnectedUserList;
import com.insa.utils.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

class UDPServerTest {

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static UDPServer server;

    @BeforeAll
    static void init() throws SocketException {
        server = new UDPServer(Constants.UDP_SERVER_PORT);
        DiscoveryController discoveryController = new DiscoveryController();
        server.addObserver(discoveryController);
        ConnectedUserList.getInstance().clear();
    }


    @AfterAll
    static void tearDown() {
        ConnectedUserList.getInstance().clear();
        UDPServer.close();
    }


    @Test
    void testRun() {
        server.start();
        assertTrue(server.isAlive());
    }


    @Test
    void testDiscovery() throws IOException {
        // Create Discovery message
        UDPMessage broadcastMsg = new UDPMessage();
        broadcastMsg.setType(UDPMessage.MessageType.DISCOVERY);
        User testDiscoveryUser = new User("testDiscovery");
        broadcastMsg.setSender(testDiscoveryUser);

        // Check that database is empty
        assertTrue(ConnectedUserList.getInstance().isEmpty());

        // Process msg
        server.dataProcessing(gson.toJson(broadcastMsg), InetAddress.getByName("255.255.255.255"));

        // Assertions based on expected changes
        assertFalse(ConnectedUserList.getInstance().isEmpty());
        assertTrue(ConnectedUserList.getInstance().hasUsername(testDiscoveryUser.getUsername()));
    }

    @Test
    void testNewUser() throws IOException {
        // Create message
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.USER_CONNECTED);
        User testNewUser = new User("testNewUser");
        message.setSender(testNewUser);

        // Check that db only contains previous user
        assertEquals(1, ConnectedUserList.getInstance().size());

        server.dataProcessing(gson.toJson(message), InetAddress.getByName("255.255.255.255"));

        // New user is added to the db
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(testNewUser.getUsername()));
    }
}