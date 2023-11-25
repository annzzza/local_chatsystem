package com.insa.GUI;

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

        MyLogger.info("Clicked changeUsername");

        if (!inputNewUsername.getText().isEmpty()){
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.notifyChangeUsername(currentUser,inputNewUsername.getText());
            changedUsernameLabel.setText("Username changed!");
        }
        else {
            MyLogger.info("Empty textfield new username");
        }

    }
}
