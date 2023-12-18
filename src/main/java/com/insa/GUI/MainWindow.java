package com.insa.GUI;


import com.insa.GUI.view.ChatClass;
import com.insa.GUI.view.ChatGUI;
import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.network.NetworkManager;
import com.insa.utils.MyLogger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow {

    private final JFrame window = new JFrame("Clavardages");
    private final JMenuBar menuBar = new JMenuBar();

    private final JTextField changeUsernameTextField = new JTextField("New Username");


    public void createBorderLayoutTop(){

        menuBar.add(new JLabel("Menu"));

        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /////DISCONNECT FUNCTION
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

        changeUsernameTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUsernameButtonHandler();
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
        ChatGUI gui = new ChatGUI();

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPane.setResizeWeight(0.33);

        MyLogger.getInstance().info("OKOKOK");

        DefaultListModel<String> chatItemList = gui.chatListBuilderCorrectVersion();

        JList listChats = new JList(chatItemList);


        jSplitPane.setLeftComponent(listChats);
        jSplitPane.setRightComponent(new Label("HELLO"));

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