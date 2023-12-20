package com.insa.GUI;


import com.insa.GUI.view.ChatClass;
import com.insa.database.FakeDatabase;
import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.network.NetworkManager;
import com.insa.utils.MyLogger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainWindow {

    private final JFrame window = new JFrame("Clavardages");
    private final JTextField changeUsernameTextField = new JTextField("New Username");



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
        changeUsernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUsernameButtonHandler();
            }
        });

        changeUsernameTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeUsernameTextField.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        menuBar.add(changeUsernameTextField);
        menuBar.add(changeUsernameButton);

        window.add(menuBar, BorderLayout.NORTH);
    }


    public void createBorderLayoutBottom(){

        JLabel bottomLabel = new JLabel("created by R.B. & A.C.", SwingConstants.LEFT);

        window.add(bottomLabel, BorderLayout.SOUTH);
    }


    public void createBorderLayoutCenter(){

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPane.setResizeWeight(0.33);

        MyLogger.getInstance().info("OKOKOK");

        DefaultListModel<String> chatItemList = chatListBuilder();

        JList listChats = new JList(chatItemList);
        listChats.setFixedCellHeight(48);
        listChats.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(listChats.getSelectedValue().toString());
                String usernameSelectedChat = listChats.getSelectedValue().toString();
                jSplitPane.setRightComponent(chattingPanelBuilder(usernameSelectedChat));
            }
        });

        jSplitPane.setLeftComponent(listChats);
        jSplitPane.setRightComponent(new Label("Vide"));

        window.add(jSplitPane, BorderLayout.CENTER);
    }

    public void start(){
        createBorderLayoutTop();
        createBorderLayoutCenter();
        createBorderLayoutBottom();

        window.setSize(800, 600);
        window.setVisible(true);
    }



    private void changeUsernameButtonHandler() {
        // Replace with your logic for handling the button click
        NetworkManager nwm = NetworkManager.getInstance();
        System.out.println("Clicked changeUsername");

        if (!changeUsernameTextField.getText().isEmpty()) {
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.notifyChangeUsername(currentUser,changeUsernameTextField.getText());
            //changedUsernameLabel.setText("Username changed!");
        } else {
            System.out.println("Empty textfield new username");
        }
    }

    private void disconnectButtonHandler(){
        NetworkManager.getInstance().sendDisconnection(new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP));
        System.exit(0);
    }


    private DefaultListModel<String> chatListBuilder(){
        List<ConnectedUser> connectedUserList = FakeDatabase.Database.makeConnectedUserList();
        DefaultListModel<String> chatList = new DefaultListModel<>();

        MyLogger.getInstance().info("building list of chats");
        for (ConnectedUser connectedUser : connectedUserList) {
            ChatClass chat = new ChatClass(connectedUser.getUsername());
            chatList.addElement(chat.toString());
        }

        return chatList;
    }

    private JPanel chattingPanelBuilder(String usernameSelectedChat){

        JPanel chattingPanel = new JPanel();
        chattingPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel topLabel = new JLabel(usernameSelectedChat);
        topPanel.add(topLabel, JPanel.CENTER_ALIGNMENT);
        topPanel.setBackground(new Color(136, 171, 142));
        chattingPanel.add(topPanel, BorderLayout.NORTH);

        JMenuBar bottomMenu = new JMenuBar();
        JTextField messageTextField = new JTextField("Type your message here:");
        messageTextField.setPreferredSize(new Dimension(250, 40));
        messageTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                messageTextField.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        bottomMenu.add(messageTextField);
        JButton sendButton = new JButton("SEND");
        sendButton.setBackground(new Color(136, 171, 142));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyLogger.getInstance().info("send message button pressed");
                //TODO: onSendButtonClicked();
            }
        });
        bottomMenu.add(sendButton);
        chattingPanel.add(bottomMenu, BorderLayout.SOUTH);


        //todo:use DAO to retreive history of chats with corresponding user


        return chattingPanel;
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