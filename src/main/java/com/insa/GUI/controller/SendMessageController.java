package com.insa.GUI.controller;

import com.insa.database.LocalDatabase;
import com.insa.network.TCPClient;
import com.insa.network.TCPMessage;
import com.insa.network.connectedusers.ConnectedUser;
import com.insa.network.connectedusers.ConnectedUserList;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;

/*
 * Controller for sending a message
 */
public class SendMessageController implements ActionListener {
    final private String usernameSelectedChat; // username of the user we want to send a message to
    final private String messageToSend; // message to send
    final private TCPClient tcpClient; // TCP client to send the message

    /*
        Constructor
        @param usernameSelectedChat: username of the user we want to send a message to
        @param messageToSend: message to send
        @param tcpClient: TCP client to send the message
     */
    public SendMessageController(String usernameSelectedChat, String messageToSend, TCPClient tcpClient) {
        super();
        this.usernameSelectedChat = usernameSelectedChat;
        this.messageToSend = messageToSend;
        this.tcpClient = tcpClient;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MyLogger.getInstance().info("Send message to " + usernameSelectedChat);

        // Get connecteduser of selected username
        ConnectedUser connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

        // Make message
        tcpClient.sendMessage(new TCPMessage(messageToSend, LocalDatabase.Database.currentUser, connectedUserSelected, new Timestamp(new Date().getTime())));
    }
}
