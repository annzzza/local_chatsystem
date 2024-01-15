package com.insa.GUI;

import com.insa.GUI.controller.SendMessageController;
import com.insa.network.TCPClient;
import com.insa.network.TCPMessage;
import com.insa.network.TCPServer;
import com.insa.network.connectedusers.ConnectedUser;
import com.insa.network.connectedusers.ConnectedUserList;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;

/*
   Panel for chatting with a user
 */
public class ChattingPanel extends JPanel implements TCPServer.TCPServerObserver, TCPClient.TCPClientObserver {

    private JPanel historyReceivedMessage;
    private JPanel historySentMessage;
    private final Color whiteBackground = new Color(242, 241, 235);

    private final TCPClient tcpClient = new TCPClient();
    final private String usernameSelectedChat;

    /*
        Constructor
        @param usernameSelectedChat: username of the user we want to send a message to
        @param tcpServer: TCP server to receive messages
     */
    ChattingPanel(String usernameSelectedChat, TCPServer tcpServer){
        super();
        this.usernameSelectedChat = usernameSelectedChat;

        // Set up connection between TCP client and TCP server
        setTcpClient();

        // Add observers
        tcpServer.addObserver(this);
        tcpClient.addObserver(this);

        // Set layout
        setLayout(new BorderLayout(10,10));

        // Make panels
        makeTopPanel();
        makeCenterPanel();
        makeBottomMenu();
    }

    /*
        Set up connection between TCP client and TCP server
     */
    private void setTcpClient() {
        ConnectedUser connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

        if(connectedUserSelected == null) {
            MyLogger.getInstance().info("No connected user found for username " + usernameSelectedChat);
            return;
        }

        try {
            MyLogger.getInstance().info("Starting TCP client to " + connectedUserSelected.getIP().toString() + ":" + Constants.TCP_SERVER_PORT);
            String ip = connectedUserSelected.getIP().toString().substring(1);
            tcpClient.startConnection(ip, Constants.TCP_SERVER_PORT);
        } catch (IOException e) {
            MyLogger.getInstance().log("Error while starting TCP client: " + e.getMessage(), Level.SEVERE);
            e.printStackTrace();
        }
    }

    /*
        Make top panel with the name of the user we are chatting with
     */
    private void makeTopPanel() {
        JPanel topPanel = new JPanel();
        JLabel topLabel = new JLabel(usernameSelectedChat);

        topPanel.add(topLabel, JPanel.CENTER_ALIGNMENT);
        topPanel.setBackground(new Color(136, 171, 142));
        add(topPanel, BorderLayout.NORTH);
    }

    /*
        Make center panel with the history of messages sent and received
     */
    private void makeCenterPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setBackground(whiteBackground);
        BorderLayout bdl = new BorderLayout(10, 10);
        historyPanel.setLayout(bdl);

        historyReceivedMessage = new JPanel();
        historySentMessage = new JPanel();
        historyReceivedMessage.setBackground(whiteBackground);
        historySentMessage.setBackground(whiteBackground);
        historyReceivedMessage.setLayout(new BoxLayout(historyReceivedMessage, BoxLayout.Y_AXIS));
        historySentMessage.setLayout(new BoxLayout(historySentMessage, BoxLayout.Y_AXIS));

        // TODO: Load history

        historyPanel.add(historyReceivedMessage, BorderLayout.WEST);
        historyPanel.add(historySentMessage, BorderLayout.EAST);
        historyPanel.setBackground(whiteBackground);
        JScrollPane historyScrollPane = new JScrollPane(historyPanel);

        add(historyScrollPane, BorderLayout.CENTER);
    }

    /*
        Make bottom menu with the message text field and the send button
     */
    private void makeBottomMenu() {
        JMenuBar bottomMenu = new JMenuBar();

        // Message text field
        PlaceholderTextField messageTextField = new PlaceholderTextField();
        messageTextField.setPlaceholder("Type your message here:");
        messageTextField.setPreferredSize(new Dimension(250, 40));
        bottomMenu.add(messageTextField);

        // Send button
        JButton sendButton = new JButton("SEND");
        sendButton.setBackground(new Color(136, 171, 142));
        SendMessageController sendMessageController = new SendMessageController(usernameSelectedChat, messageTextField.getText(), tcpClient);
        sendButton.addActionListener(sendMessageController);
        bottomMenu.add(sendButton);

        add(bottomMenu, BorderLayout.SOUTH);
    }

    /*
        * Update history of messages when receiving a message
        * @param message to be added to history
     */
    @Override
    public void onNewMessage(TCPMessage message) {
        // If message is sent by the user selected in the left panel
        if (message.sender().getUsername().equals(usernameSelectedChat)) {
            MyLogger.getInstance().info("Display message received from " + message.sender().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());
            // Add message to history of received messages
            historyReceivedMessage.add(new JLabel("<html> " + message.content() + "<br/>" + message.date() + "</html>"));
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));

            // We add empty labels to keep the same size as the sent messages
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));
        }
    }

    /*
        * Update history of messages when sending a message
        * @param message to be added to history
     */
    @Override
    public void sendMessage(TCPMessage message) {
        // If message is received by the user selected in the left panel
        if (message.receiver().getUsername().equals(usernameSelectedChat)) {
            MyLogger.getInstance().info("Display message sent to " + message.receiver().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());
            // Add message to history panel of sent messages
            historySentMessage.add(new JLabel("<html> " + message.content() + "<br/>" + message.date() + "</html>"));
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));

            // We add empty labels to keep the same size as the received messages
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));

            // Refresh history panel
            historySentMessage.revalidate();
            historySentMessage.repaint();
        }
    }
}
