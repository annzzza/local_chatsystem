package com.insa.gui.controller;

import com.insa.database.HistoryDAO;
import com.insa.gui.view.PlaceholderTextField;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.discovery.UsernameAlreadyTakenException;
import com.insa.users.User;
import com.insa.utils.MyLogger;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/*
 * Controller for changing username
 */
public class ChangeUsernameController implements ActionListener {
    private static final MyLogger LOGGER = new MyLogger(ChangeUsernameController.class.getName());


    private final PlaceholderTextField newUsernameTextField;
    private String oldUsername;
    private final HistoryDAO historyDAO = new HistoryDAO();


    /**
        Constructor
        @param newUsernameTextField: textfield for new username
     */
    public ChangeUsernameController(PlaceholderTextField newUsernameTextField, String username) {
        super();
        this.newUsernameTextField = newUsernameTextField;
        this.oldUsername = username;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        DiscoveryManager nwm = DiscoveryManager.getInstance();

        if (!newUsernameTextField.getText().isEmpty()) {
            try {
                LOGGER.info("ChangeUsername button clicked, request transferred to network manager");
                nwm.sendChangeUsername(nwm.getCurrentUser(), newUsernameTextField.getText());

                // Add to history
                historyDAO.updateHistoryDB(new User(oldUsername), nwm.getCurrentUser().getUsername());
                LOGGER.fine("History updated with new username: " + nwm.getCurrentUser().getUsername());
            } catch (UsernameAlreadyTakenException e) {
                LOGGER.severe("Username already taken. Not changed.");
                JOptionPane.showMessageDialog(null, "Username already taken:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                LOGGER.severe("SQL Error, history not updated after username change:\n" + e.getMessage());
                JOptionPane.showMessageDialog(null, "History not updated. You will loose your history.\n" + e.getMessage(), "Error :(", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            LOGGER.info("Empty textfield new username");
        }
    }
}
