package com.insa.gui.controller;

import com.insa.gui.view.PlaceholderTextField;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.discovery.UsernameAlreadyTakenException;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeUsernameControllerTest {

    @Test
    void actionPerformed() throws UsernameAlreadyTakenException, ConnectedUserAlreadyExists {
        // Fake connection
        DiscoveryManager.getInstance().discoverNetwork("user");

        // Fake other user connected
        ConnectedUserList.getInstance().addConnectedUser(new ConnectedUser("user2", null));

        PlaceholderTextField placeholderTextField = new PlaceholderTextField();
        placeholderTextField.setText("user1");

        ChangeUsernameController changeUsernameController = new ChangeUsernameController(placeholderTextField);

        // Change username to user1 (not taken)
        changeUsernameController.actionPerformed(null);
        assertEquals("user1", DiscoveryManager.getInstance().getCurrentUser().getUsername());

    }
}