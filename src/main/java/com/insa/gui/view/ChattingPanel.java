package com.insa.gui.view;

import com.insa.database.HistoryDAO;
import com.insa.gui.controller.AddMessageToHistory;
import com.insa.gui.controller.SendMessageController;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPMessage;
import com.insa.network.tcp.TCPServer;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/*
   Panel for chatting with a user
 */
public class ChattingPanel extends JPanel implements TCPServer.TCPServerObserver, TCPClient.TCPClientObserver {

    private static final MyLogger LOGGER = new MyLogger(ChattingPanel.class.getName());

    private JPanel historyReceivedMessage;
    private JPanel historySentMessage;
    private final Color whiteBackground = new Color(242, 241, 235);

    private final TCPClient tcpClient = new TCPClient();
    final private String usernameSelectedChat;
    private final ConnectedUser connectedUserSelected;

    private final String usernameSelf;


    private final HistoryDAO historyDAO;

    /*
        Constructor
        @param usernameSelectedChat: username of the user we want to send a message to
        @param tcpServer: TCP server to receive messages
     */
    public ChattingPanel(String usernameSelectedChat, String usernameSelf){
        super();
        this.usernameSelectedChat = usernameSelectedChat;
        this.usernameSelf = usernameSelf;
        this.historyDAO = new HistoryDAO();

        connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

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

    /*
        Set up connection between TCP client and TCP server
     */
    private void setTcpClient() {
       // ConnectedUser connectedUserSelected = ConnectedUserList.getInstance().getConnectedUser(usernameSelectedChat);

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
        try {
            ArrayList<TCPMessage> historyList = historyDAO.getHistoryWith(connectedUserSelected, new User(usernameSelf));
            for (TCPMessage message : historyList){
                //Add message to history panel
                addMessage(message);
            }
        } catch (SQLException e) {
            LOGGER.severe("Could not retrieve History with " + usernameSelectedChat);
            //throw new RuntimeException(e);
        }


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
        SendMessageController sendMessageController = new SendMessageController(usernameSelectedChat, messageTextField, tcpClient);
        sendButton.addActionListener(sendMessageController);
        bottomMenu.add(sendButton);

        add(bottomMenu, BorderLayout.SOUTH);
    }

    /*
        Add message content and date to the panel it belongs
        @param message TCPmessage either sent or received by the selected user
     */
    private void addMessage(TCPMessage message) {
        LOGGER.info("in function addMessage:");

        // If message is sent by the user selected in the left panel
        if (message.sender().getUsername().equals(usernameSelectedChat)) {
            LOGGER.info("Display message received from " + message.sender().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());
            // Add message to history of received messages
            historyReceivedMessage.add(new JLabel("<html> " + message.content() + "<br/>" + message.date() + "</html>"));
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));

            // We add empty labels to keep the same size as the sent messages
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));

        }
        // If message is received by the user selected in the left panel
        else if (message.receiver().getUsername().equals(usernameSelectedChat)) {
            LOGGER.info("Display message sent to " + message.receiver().getUsername() + " in chat with " + usernameSelectedChat + ": " + message.content() + " at " + message.date());
            // Add message to history panel of sent messages
            historySentMessage.add(new JLabel("<html> " + message.content() + "<br/>" + message.date() + "</html>"));
            historySentMessage.add(new JLabel(("<html><br/><br/></html>")));

            // We add empty labels to keep the same size as the received messages
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));
            historyReceivedMessage.add(new JLabel(("<html><br/><br/></html>")));
        }
        // Refresh history panel
        historySentMessage.revalidate();
        historySentMessage.repaint();
    }

    /*
        * Update history of messages when receiving a message
        * @param message to be added to history
     */
    @Override
    public void onNewMessage(TCPMessage message) {
        LOGGER.info("in function onNewMessage:");
        //Add message to history panel
        addMessage(message);
    }
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
