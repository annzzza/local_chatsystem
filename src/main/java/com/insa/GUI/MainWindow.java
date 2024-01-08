package com.insa.GUI;

import com.insa.GUI.view.ChatClass;
import com.insa.database.FakeDatabase;
import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.network.Message;
import com.insa.network.NetworkManager;
import com.insa.utils.MyLogger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {

    private final JFrame window = new JFrame("Clavardages");
    private final JTextField changeUsernameTextField = new JTextField("New Username");

    private final Color whiteBackground = new Color(242, 241, 235);


    /**
     * GUI for top Menu of the application
     */
    public void createBorderLayoutTop(){

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(true);

        menuBar.add(new JLabel("Menu"));

        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectButtonHandler();
            }
        });

        menuBar.add(disconnectMenuItem);
        JButton changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setBackground(whiteBackground);
        changeUsernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUsernameButtonHandler();
                changeUsernameTextField.setText("Username successfully changed !");
            }
        });

        //Mouse listener: clears textfield when textfield is clicked
        changeUsernameTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeUsernameTextField.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        menuBar.add(changeUsernameTextField);
        menuBar.add(changeUsernameButton);

        //display the menu on the top part of the border layout
        window.add(menuBar, BorderLayout.NORTH);
    }


    /**
     * GUI for bottom creator credit of the application
     */
    public void createBorderLayoutBottom(){
        JLabel bottomLabel = new JLabel("created by R.B. & A.C.", SwingConstants.LEFT);
        bottomLabel.setBackground(new Color(136, 171, 142));

        window.add(bottomLabel, BorderLayout.SOUTH);
    }


    /**
     * GUI for center page of the application
     */
    public void createBorderLayoutCenter() {

        //SplitPane: Screen is divided in two horizontally
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPane.setResizeWeight(0.33);

        //Left panel displays the clickable list of connected users
        DefaultListModel<String> chatItemList = chatListBuilder();

        JList listChats = new JList(chatItemList);
        listChats.setFixedCellHeight(48);
        listChats.setBackground(whiteBackground);

        //Right panel displays the chat (history+send message) with selected user from left panel
        listChats.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String usernameSelectedChat = listChats.getSelectedValue().toString();
                jSplitPane.setRightComponent(chattingPanelBuilder(usernameSelectedChat));
            }
        });

        //chatItemList.addElement("jonan");

        jSplitPane.setLeftComponent(new JScrollPane(listChats));
        //Default right panel
        Label defaultRightPanel = new Label("Select a user to chat with!");
        defaultRightPanel.setBackground(whiteBackground);

        jSplitPane.setRightComponent(defaultRightPanel);

        window.add(jSplitPane, BorderLayout.CENTER);
    }

    public void start(){
        //ImageIcon img = new ImageIcon("src/main/java/com/insa/GUI/iconChatSystem.png");
        //window.setIconImage(img.getImage());
        createBorderLayoutTop();
        createBorderLayoutCenter();
        createBorderLayoutBottom();

        window.setSize(800, 600);
        window.setVisible(true);
    }


    /**
     * Handler for changeUsernameButton: sends changeUSername request to networkManager
     */
    private void changeUsernameButtonHandler() {
        NetworkManager nwm = NetworkManager.getInstance();

        if (!changeUsernameTextField.getText().isEmpty()) {
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.notifyChangeUsername(currentUser,changeUsernameTextField.getText());
            //changedUsernameLabel.setText("Username changed!");

            MyLogger.getInstance().info("ChangeUsername button clicked, request transferred to network manager");

        } else {
            MyLogger.getInstance().info("Empty textfield new username");
        }
    }

    /**
     * Handler for disconnectButton: sends disconnect request to networkManager
     */
    private void disconnectButtonHandler(){
        NetworkManager.getInstance().sendDisconnection(new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP));
        System.exit(0);
    }


    /**
     * @return DefaultListModel of String to be inserted in JList on the left panel
     */
    private DefaultListModel<String> chatListBuilder(){
        //Retreive list of Connected Users
        List<ConnectedUser> connectedUserList = FakeDatabase.Database.makeConnectedUserList();
        DefaultListModel<String> chatList = new DefaultListModel<>();


        MyLogger.getInstance().info("building list of chats");

        //for each Connected User, adds their name to the DefaultListModel
        for (ConnectedUser connectedUser : connectedUserList) {
            ChatClass chat = new ChatClass(connectedUser.getUsername());
            chatList.addElement(chat.toString());
        }

        return chatList;
    }

    /**
     * @param usernameSelectedChat username of the connected user that is selected on left panel
     * @return corresponding right panel GUI
     */
    private JPanel chattingPanelBuilder(String usernameSelectedChat){

        JPanel chattingPanel = new JPanel();
        chattingPanel.setLayout(new BorderLayout(10, 10));

        //top of Right panel: name of the selected user
        JPanel topPanel = new JPanel();
        JLabel topLabel = new JLabel(usernameSelectedChat);
        topPanel.add(topLabel, JPanel.CENTER_ALIGNMENT);
        topPanel.setBackground(new Color(136, 171, 142));
        chattingPanel.add(topPanel, BorderLayout.NORTH);

        //bottom of Right panel: input message and send message button
        JMenuBar bottomMenu = new JMenuBar();
        JTextField messageTextField = new JTextField("Type your message here:");
        messageTextField.setPreferredSize(new Dimension(250, 40));
        messageTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                messageTextField.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        bottomMenu.add(messageTextField);
        JButton sendButton = new JButton("SEND");
        sendButton.setBackground(new Color(136, 171, 142));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSendButtonClicked(usernameSelectedChat);
            }
        });
        bottomMenu.add(sendButton);
        chattingPanel.add(bottomMenu, BorderLayout.SOUTH);


        //todo:use DAO to retreive history of chats with corresponding user
        //something like: ArrayList<Message> historyMessagesList = LocalDatabase.Database.getHistoryMessages(usernameSelectedChat);

        JPanel historyPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0, 2, 10, 10);
        BorderLayout bdl = new BorderLayout(10, 10);
        historyPanel.setLayout(bdl);


        //TESTS
        ArrayList<Message> testListChatsMessage = FakeDatabase.Database.getHistoryMessages();

        JPanel receivedMessage = new JPanel();
        JPanel sentMessage = new JPanel();
        receivedMessage.setBackground(whiteBackground);
        sentMessage.setBackground(whiteBackground);
        receivedMessage.setLayout(new BoxLayout(receivedMessage, BoxLayout.Y_AXIS));
        sentMessage.setLayout(new BoxLayout(sentMessage, BoxLayout.Y_AXIS));

        for (int j=0; j<4; j++){
            for (Message i : testListChatsMessage) {
                //test if sender is localhost or not -> saved UUID entry of ones's self in LocalDatabase?
                if (i.getSender().getUsername().equals("anna")) {
                    receivedMessage.add(new JLabel("<html> " + i.getContent() + "<br/>" + i.getDate() + "</html>"));
                    sentMessage.add(new JLabel(("<html><br/></html>")));
                } else {
                    sentMessage.add(new JLabel("<html> " + i.getContent() + "<br/>" + i.getDate() + "</html>"));
                    receivedMessage.add(new JLabel(("<html><br/></html>")));
                }
                receivedMessage.add(new JLabel(("<html><br/><br/></html>")));
                sentMessage.add(new JLabel(("<html><br/><br/></html>")));

            }
        }

        historyPanel.add(receivedMessage, BorderLayout.EAST);
        historyPanel.add(sentMessage, BorderLayout.WEST);


        historyPanel.setBackground(whiteBackground);

        JScrollPane historyScrollPane = new JScrollPane(historyPanel);
        chattingPanel.add(historyScrollPane, BorderLayout.CENTER);

        return chattingPanel;
    }

    public void onSendButtonClicked(String usernameSelectedChat){
        MyLogger.getInstance().info("Send message to " + usernameSelectedChat);
        //send TCP message to selected username
        //update history of messages
        //update GUI with new message sent
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow().start();
        });
    }

}





/*
import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.network.NetworkManager;
import com.insa.network.ConnectedUser;
import com.insa.utils.MyLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.stage.Stage;

import java.awt.*;

public class SecondWindow {
    Stage window = new Stage();
    GridPane grid = new GridPane();
    TextField inputNewUsername = new TextField();
   Button changeUsernameButton = new Button("Change Username");
   Label changedUsernameLabel = new Label("");

    public void start(){
        window.setTitle("Chats");

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        GridPane.setColumnIndex(inputNewUsername, 0);
        GridPane.setRowIndex(inputNewUsername, 0);
        grid.getChildren().add(inputNewUsername);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        GridPane.setColumnIndex(buttonBox, 0);
        GridPane.setRowIndex(buttonBox, 1);

        changeUsernameButton.setOnAction(e->changeUsernameButtonHandler());
        buttonBox.getChildren().add(changeUsernameButton);
        grid.getChildren().add(buttonBox);

        GridPane.setColumnIndex(changedUsernameLabel, 0);
        GridPane.setRowIndex(changedUsernameLabel, 2);
        grid.getChildren().add(changedUsernameLabel);

        Scene scene = new Scene(grid, 400, 275);
        window.setScene(scene);

        window.show();
    }


    private void changeUsernameButtonHandler(){
        NetworkManager nwm = NetworkManager.getInstance();

        MyLogger.getInstance().info("Clicked changeUsername");

        if (!inputNewUsername.getText().isEmpty()){
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.notifyChangeUsername(currentUser,inputNewUsername.getText());
            changedUsernameLabel.setText("Username changed!");
        }
        else {
            MyLogger.getInstance().info("Empty textfield new username");
        }

    }
}
*/