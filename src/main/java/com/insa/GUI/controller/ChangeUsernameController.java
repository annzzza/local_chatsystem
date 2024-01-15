package com.insa.GUI.controller;

import com.insa.database.LocalDatabase;
import com.insa.network.NetworkManager;
import com.insa.network.connectedusers.ConnectedUser;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Controller for changing username
 */
public class ChangeUsernameController implements ActionListener {

    String newUsername;

    /*
        Constructor
        @param newUsername: textfield for new username
     */
    public ChangeUsernameController(String newUsername) {
        super();
        this.newUsername = newUsername;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        NetworkManager nwm = NetworkManager.getInstance();

        if (!newUsername.isEmpty()) {
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.notifyChangeUsername(currentUser, newUsername);
            MyLogger.getInstance().info("ChangeUsername button clicked, request transferred to network manager");
        } else {
            MyLogger.getInstance().info("Empty textfield new username");
        }
    }
}
