package com.insa.database;

import com.insa.network.tcp.TCPMessage;
import com.insa.users.ConnectedUser;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUsernameInDBTest {
    @Test
    void usernameChangedTest() throws DAOException {
        // Create user
        UpdateUsernameInDB updateUsernameInDB = new UpdateUsernameInDB();
        String newUsername = "newUsername";
        ConnectedUser connectedUser = new ConnectedUser(newUsername, InetAddress.getLoopbackAddress());

        // Add history with old username
        String oldUsername = "oldUsername";

        HistoryDAO historyDAO = new HistoryDAO();
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "Hello", connectedUser, connectedUser,  new Timestamp(new Date().getTime()));
        historyDAO.addToHistoryDB(msg);

        // Change username
        updateUsernameInDB.usernameChanged(connectedUser, oldUsername);

        // Get history
        ArrayList<TCPMessage> history = historyDAO.getHistoryWith(connectedUser, connectedUser);

        // Check that the username has been changed
        history.stream()
                .filter(message -> message.sender().getUsername().equals(newUsername)
                        && message.receiver().getUsername().equals(newUsername))
                .forEach(message -> {
                    assertEquals(newUsername, message.sender().getUsername());
                    assertEquals(newUsername, message.receiver().getUsername());
                });
    }

}