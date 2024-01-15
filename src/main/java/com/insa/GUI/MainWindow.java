package com.insa.GUI;

import com.insa.GUI.controller.ChangeUsernameController;
import com.insa.GUI.controller.DisconnectionController;
import com.insa.GUI.view.ContactView;
import com.insa.network.TCPServer;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

/**
 * Main window of the application
 */
public class MainWindow {

    private final JFrame window = new JFrame("Clavardages");

    private final Color whiteBackground = new Color(242, 241, 235);

    private TCPServer tcpServer;

    /**
     * GUI for top Menu of the application
     */
    public void createBorderLayoutTop() {
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(true);

        // Menu for disconnection
        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
        DisconnectionController disconnectionController = new DisconnectionController(tcpServer);
        disconnectMenuItem.addActionListener(disconnectionController);
        menuBar.add(disconnectMenuItem);


        // Menu for changing username

        // Text field for new username
        PlaceholderTextField changeUsernameTextField = new PlaceholderTextField();
        changeUsernameTextField.setPlaceholder("New Username");
        ChangeUsernameController changeUsernameController = new ChangeUsernameController(changeUsernameTextField.getText());
        menuBar.add(changeUsernameTextField);

        // Button for changing username
        JButton changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setBackground(whiteBackground);
        changeUsernameButton.addActionListener(changeUsernameController);
        menuBar.add(changeUsernameButton);

        // Display the menu on the top part of the border layout
        window.add(menuBar, BorderLayout.NORTH);
    }


    /**
     * GUI for bottom creator credit of the application
     */
    public void createBorderLayoutBottom() {
        JLabel bottomLabel = new JLabel("created by R.B. & A.C.", SwingConstants.LEFT);
        bottomLabel.setBackground(new Color(136, 171, 142));

        window.add(bottomLabel, BorderLayout.SOUTH);
    }


    /**
     * GUI for center page of the application
     */
    public void createBorderLayoutCenter() {

        // SplitPane: Screen is divided in two horizontally
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPane.setResizeWeight(0.33);

        // Left panel displays the clickable list of connected users
        ContactView contactView = ContactView.getInstance();
        ContactView.initialize();
        JList listChats = new JList(contactView);
        listChats.setFixedCellHeight(48);
        listChats.setBackground(whiteBackground);

        //Right panel displays the chat (history+send message) with selected user from left panel
        listChats.addListSelectionListener(e -> {
            String usernameSelectedChat = listChats.getSelectedValue().toString();
            ChattingPanel chattingPanel = new ChattingPanel(usernameSelectedChat, tcpServer);
            jSplitPane.setRightComponent(chattingPanel);
        });

        //chatItemList.addElement("jonan");

        jSplitPane.setLeftComponent(new JScrollPane(listChats));
        //Default right panel
        Label defaultRightPanel = new Label("Select a user to chat with!");
        defaultRightPanel.setBackground(whiteBackground);

        jSplitPane.setRightComponent(defaultRightPanel);

        window.add(jSplitPane, BorderLayout.CENTER);
    }

    public void start() {
        //ImageIcon img = new ImageIcon("src/main/java/com/insa/GUI/iconChatSystem.png");
        //window.setIconImage(img.getImage());
        createBorderLayoutTop();
        createBorderLayoutCenter();
        createBorderLayoutBottom();

        window.setSize(800, 600);
        window.setVisible(true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                DisconnectionController disconnectionController = new DisconnectionController(tcpServer);
                disconnectionController.actionPerformed(null);

                System.exit(0);
            }
        });

        // Start TCP server
        try {
            MyLogger.getInstance().info("Starting TCP server on port " + Constants.TCP_SERVER_PORT + "...");
            tcpServer = new TCPServer(Constants.TCP_SERVER_PORT);
            tcpServer.start();

            // Add observer to TCP server
            tcpServer.addObserver((message) -> MyLogger.getInstance().info("Received message: " + message));
        } catch (Exception e) {
            MyLogger.getInstance().log("Error while starting TCP server: " + e.getMessage(), Level.SEVERE);
            System.err.println("Error while starting TCP server: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().start());
    }
}