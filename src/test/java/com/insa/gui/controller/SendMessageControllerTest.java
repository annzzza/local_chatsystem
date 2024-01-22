package com.insa.gui.controller;

import com.insa.gui.view.PlaceholderTextField;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.discovery.UsernameAlreadyTakenException;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPServer;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageControllerTest {

    String usernameSelectedChat; // username of the user we want to send a message to
    PlaceholderTextField placeholderTextField; // message to send
    TCPClient tcpClient = new TCPClient(); // TCP client to send the message

    SendMessageController sendMessageController;

    @BeforeAll
    static void setUp() {
        ConnectedUserList.getInstance().clear();
    }
    @Test
    void testSendMessageController() throws ConnectedUserAlreadyExists, IOException, UsernameAlreadyTakenException {
        //  Setup
        // Fake discovery so that the user is connected
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.discoverNetwork("test");

        usernameSelectedChat = "userConnectedSendMessageControllerTest";
        placeholderTextField = new PlaceholderTextField("message");
        TCPServer tcpServer = new TCPServer(7452);
        tcpServer.start();
        tcpClient.startConnection("127.0.0.1", 7452);

        sendMessageController = new SendMessageController(usernameSelectedChat, placeholderTextField, tcpClient);

        // Fake connected user
        // Should be the same as usernameSelectedChat
        ConnectedUser connectedUser = new ConnectedUser("userConnectedSendMessageControllerTest", InetAddress.getLoopbackAddress());
        ConnectedUserList.getInstance().addConnectedUser(connectedUser);


        //  Test that the message is sent
        tcpClient.addObserver(message -> {
            if(message.receiver().equals(connectedUser)) {
                assertEquals(message.content(), placeholderTextField.getText());
            }
        });

        sendMessageController.actionPerformed(null);
        assertEquals(placeholderTextField.getText(), "");

        // Test that usernameselection is updated
        ConnectedUserList.getInstance().changeUsername(connectedUser, "newUsername");
        assertEquals("newUsername", sendMessageController.usernameSelectedChat);
    }

    @AfterAll
    static void tearDown() {
        ConnectedUserList.getInstance().clear();
    }
}