package com.insa.gui.controller;

import com.insa.database.HistoryDAO;
import com.insa.gui.view.PlaceholderTextField;
import com.insa.database.LocalDatabase;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/*
 * Controller for changing username
 */
public class ChangeUsernameController implements ActionListener {
    private static final MyLogger LOGGER = new MyLogger(ChangeUsernameController.class.getName());


    PlaceholderTextField newUsernameTextField;
    String oldUsername;
    HistoryDAO historyDAO = new HistoryDAO();

    /*
        Constructor
        @param newUsername: textfield for new username
     */
    public ChangeUsernameController(PlaceholderTextField newUsernameTextField, String username) {
        super();
        this.newUsernameTextField = newUsernameTextField;
        this.oldUsername = username;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        DiscoveryManager nwm = DiscoveryManager.getInstance();

        if (!newUsernameTextField.getText().isEmpty() && !ConnectedUserList.getInstance().hasUsername(newUsernameTextField.getText())) {
            ConnectedUser currentUser = new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP);
            nwm.sendChangeUsername(currentUser, newUsernameTextField.getText());
            LOGGER.info("ChangeUsername button clicked, request transferred to network manager");
            try {
                historyDAO.updateHistoryDB(new User(oldUsername), newUsernameTextField.getText());
                LOGGER.info("History updated with new username: " + newUsernameTextField.getText());
            } catch (SQLException e) {
                LOGGER.severe("Could not update history DB.");
                //throw new RuntimeException(e);
            }
        } else {
            LOGGER.info("Empty textfield new username");
        }
    }
}
