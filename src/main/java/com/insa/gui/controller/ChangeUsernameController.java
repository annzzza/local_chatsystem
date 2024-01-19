package com.insa.gui.controller;

import com.insa.gui.view.PlaceholderTextField;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.discovery.UsernameAlreadyTakenException;
import com.insa.utils.MyLogger;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Controller for changing username
 */
public class ChangeUsernameController implements ActionListener {
    private static final MyLogger LOGGER = new MyLogger(ChangeUsernameController.class.getName());


    private final PlaceholderTextField newUsernameTextField;


    /**
        Constructor
        @param newUsernameTextField: textfield for new username
     */
    public ChangeUsernameController(PlaceholderTextField newUsernameTextField) {
        this.newUsernameTextField = newUsernameTextField;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        DiscoveryManager nwm = DiscoveryManager.getInstance();

        if (!newUsernameTextField.getText().isEmpty()) {
            try {
                LOGGER.info("ChangeUsername button clicked, request transferred to network manager");
                nwm.sendChangeUsername(nwm.getCurrentUser(), newUsernameTextField.getText());
            } catch (UsernameAlreadyTakenException e) {
                JOptionPane.showMessageDialog(null, "Username already taken:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            LOGGER.info("Empty textfield new username");
        }
    }
}
