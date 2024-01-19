package com.insa.gui.view;

import com.insa.database.HistoryDAO;
import com.insa.gui.controller.AddMessageToHistory;
import com.insa.gui.controller.SendMessageController;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPMessage;
import com.insa.network.tcp.TCPServer;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
   Panel that displays history with selected user and allows to send messages.
 */
public class ChattingPanel extends JPanel implements TCPServer.TCPServerObserver, TCPClient.TCPClientObserver {

    private static final MyLogger LOGGER = new MyLogger(ChattingPanel.class.getName());

    private final Color whiteBackground = new Color(242, 241, 235);

    private final TCPClient tcpClient = new TCPClient();

    private String usernameSelectedChat;

    private final ConnectedUser connectedUserSelected;

    private JTextPane historyPane;

    private final HistoryDAO historyDAO;

    /**
        Constructor
        @param usernameSelected: username of the user we want to send a message to
     */
    public ChattingPanel(String usernameSelected){
        super();
        this.usernameSelectedChat = usernameSelected;
        this.historyDAO = new HistoryDAO();

        connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

        ConnectedUserList cul = ConnectedUserList.getInstance();
        cul.addObserver(new ConnectedUserList.Observer() {
            @Override
            public void newConnectedUser(ConnectedUser connectedUser) {

            }

            @Override
            public void removeConnectedUser(ConnectedUser connectedUser) {

            }

            @Override
            public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
                usernameSelectedChat = newConnectedUser.getUsername();
            }
        });

        // Set up connection between TCP client and TCP server
        setTcpClient();

        // Add observers
        tcpClient.addObserver(this);
        tcpClient.addObserver(new AddMessageToHistory());

        // Set layout
        setLayout(new BorderLayout(10,10));

        // Make panels
        makeTopPanel();
        makeCenterPanel();
        makeBottomMenu();
    }

    /**
        Set up connection between TCP client and TCP server
     */
    private void setTcpClient() {
        if(connectedUserSelected == null) {
            LOGGER.info("No connected user found for username " + usernameSelectedChat);
            return;
        }

        try {
            LOGGER.info("Starting TCP client to " + connectedUserSelected.getIP().toString() + ":" + Constants.TCP_SERVER_PORT);
            String ip = connectedUserSelected.getIP().toString().substring(1);
            tcpClient.startConnection(ip, Constants.TCP_SERVER_PORT);
        } catch (IOException e) {
            LOGGER.severe("Error while starting TCP client: " + e.getMessage());
        }
    }

    /**
        Make top panel with the name of the user we are chatting with
     */
    private void makeTopPanel() {
        JPanel topPanel = new JPanel();
        JLabel topLabel = new JLabel(usernameSelectedChat);

        topPanel.add(topLabel, JPanel.CENTER_ALIGNMENT);
        topPanel.setBackground(new Color(136, 171, 142));
        add(topPanel, BorderLayout.NORTH);
    }

    /**
        Make center panel with the history of messages sent and received
     */
    private void makeCenterPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setBackground(whiteBackground);
        BorderLayout bdl = new BorderLayout(10, 10);
        historyPanel.setLayout(bdl);

        historyPane = new JTextPane();
        historyPane.setBackground(whiteBackground);

        // Load history
        try {

            ArrayList<TCPMessage> historyList = historyDAO.getHistoryWith(connectedUserSelected, DiscoveryManager.getInstance().getCurrentUser());
            for (TCPMessage message : historyList){
                //Add message to history panel
                addMessage(message);
            }
        } catch (SQLException e) {
            LOGGER.severe("Could not retrieve History with " + usernameSelectedChat);
            //throw new RuntimeException(e);
        }


        JScrollPane historyScrollPane = new JScrollPane(historyPane);
        add(historyScrollPane, BorderLayout.CENTER);
    }

    /**
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
        SendMessageController sendMessageController = new SendMessageController(usernameSelectedChat, messageTextField, tcpClient);
        sendButton.addActionListener(sendMessageController);
        bottomMenu.add(sendButton);

        add(bottomMenu, BorderLayout.SOUTH);
    }

    /**
        Add message content and date to the panel it belongs
        @param message message either sent or received by the selected user
     */
    private void addMessage(TCPMessage message) {
        LOGGER.info("in function addMessage:");

        // Create a StyledDocument for the JTextPane
        StyledDocument doc = historyPane.getStyledDocument();

        // If message is received by the user selected in the left panel
        if (message.receiver().getUsername().equals(usernameSelectedChat)) {
            LOGGER.info("Display message received from " + message.sender().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());

            // Style for right-aligned text
            SimpleAttributeSet rightAlign = new SimpleAttributeSet();
            StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
            doc.setParagraphAttributes(doc.getLength(), 1, rightAlign, false);

            // Append the message content to the document with right alignment
            try {
                doc.insertString(doc.getLength(), message.content() + "\n" + message.date() + "\n", rightAlign);
            } catch (BadLocationException e) {
                LOGGER.severe("Exception while inserting message : " + e.getMessage());
            }

        } else if (message.sender().getUsername().equals(usernameSelectedChat)) {
            LOGGER.info("Display message sent to " + message.receiver().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());

            // Style for left-aligned text
            SimpleAttributeSet leftAlign = new SimpleAttributeSet();
            StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
            doc.setParagraphAttributes(doc.getLength(), 1, leftAlign, false);


            // Append the message content to the document with left alignment
            try {
                doc.insertString(doc.getLength(), message.content() + "\n" + message.date() + "\n", leftAlign);
            } catch (BadLocationException e) {
                LOGGER.severe("Exception while inserting message : " + e.getMessage());
            }
        }
    }

    /**
        * TCPServer handler on new message received
        * @param message to be processed
     */
    @Override
    public void onNewMessage(TCPMessage message) {
        LOGGER.info("in function onNewMessage:");
        //Add message to history panel
        addMessage(message);
    }

    /**
     * TCPClient handler for messages sent
     * @param message the message that was sent
     */
    /*
        * Update history of messages when sending a message
        * @param message to be added to history
     */
    @Override
    public void sendMessage(TCPMessage message) {
        LOGGER.info("in function sendMessage:");
        //Add message to history panel
        addMessage(message);
    }

}
