package com.insa.gui.controller;

import com.insa.gui.view.PlaceholderTextField;
import com.insa.database.LocalDatabase;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPMessage;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/*
 * Controller for sending a message
 */
public class SendMessageController implements ActionListener {
    private static final MyLogger LOGGER = new MyLogger(SendMessageController.class.getName());

    final private String usernameSelectedChat; // username of the user we want to send a message to
    final private PlaceholderTextField placeholderTextField; // message to send
    final private TCPClient tcpClient; // TCP client to send the message

    /*
        Constructor
        @param usernameSelectedChat: username of the user we want to send a message to
        @param messageToSend: message to send
        @param tcpClient: TCP client to send the message
     */
    public SendMessageController(String usernameSelectedChat, PlaceholderTextField messageToSendField, TCPClient tcpClient) {
        super();
        this.usernameSelectedChat = usernameSelectedChat;
        this.placeholderTextField = messageToSendField;
        this.tcpClient = tcpClient;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        LOGGER.info("Send message to " + usernameSelectedChat);

        // Get connecteduser of selected username
        ConnectedUser connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

        // Make message
        tcpClient.sendMessage(new TCPMessage(UUID.randomUUID(), placeholderTextField.getText(), LocalDatabase.Database.currentUser, connectedUserSelected, new Timestamp(new Date().getTime())));
        placeholderTextField.setText("");
    }
}
