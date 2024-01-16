package com.insa.gui.controller;

import com.insa.gui.view.PlaceholderTextField;
import com.insa.database.LocalDatabase;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.users.ConnectedUser;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Controller for changing username
 */
public class ChangeUsernameController implements ActionListener {
    private static final MyLogger LOGGER = new MyLogger(ChangeUsernameController.class.getName());


    PlaceholderTextField newUsernameTextField;

    /*
        Constructor
        @param newUsername: textfield for new username
     */
    public ChangeUsernameController(PlaceholderTextField newUsernameTextField) {
        super();
        this.newUsernameTextField = newUsernameTextField;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        DiscoveryManager nwm = DiscoveryManager.getInstance();

        if (!newUsernameTextField.getText().isEmpty()) {
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.sendChangeUsername(currentUser, newUsernameTextField.getText());
            LOGGER.info("ChangeUsername button clicked, request transferred to network manager");
        } else {
            LOGGER.info("Empty textfield new username");
        }
    }
}
