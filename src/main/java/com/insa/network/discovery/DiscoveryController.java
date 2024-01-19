package com.insa.network.discovery;

import com.insa.database.HistoryDAO;
import com.insa.database.LocalDatabase;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import java.sql.SQLException;

public class DiscoveryController implements UDPServer.Observer {

    private static final MyLogger LOGGER = new MyLogger(DiscoveryController.class.getName());
    private HistoryDAO historyDAO = new HistoryDAO();

    private void displayConnectedUsers() {
        LOGGER.info("Connected users:");
        for (ConnectedUser user : ConnectedUserList.getInstance()) {
            LOGGER.info("\t\t" + user.getUsername());
        }
    }

    @Override
    public void onNewUserConnected(ConnectedUser user) {
        try {
            if (LocalDatabase.Database.currentUser != null && LocalDatabase.Database.currentUser.getUsername().equals(user.getUsername())) {
                LOGGER.info("New user tried to connect with the same username as the current user, has not been added to connectedUserList");
                return;
            }
            ConnectedUserList.getInstance().addConnectedUser(user);
            LOGGER.info("Added user to connectedUserList: " + user);
        } catch (ConnectedUserAlreadyExists e) {
            LOGGER.info("Received duplicated connectedUser: " + e);
        }
        displayConnectedUsers();
    }

    @Override
    public void onAnswerReceived(ConnectedUser user) {
        try {
            ConnectedUserList.getInstance().addConnectedUser(user);

            LOGGER.info("Added user to connectedUserList: " + user);
        } catch (ConnectedUserAlreadyExists e) {
            LOGGER.info("Received duplicated connectedUser: " + e);
        }
        displayConnectedUsers();
    }

    @Override
    public void onUsernameChanged(ConnectedUser user, String newUsername) {
        if (!ConnectedUserList.getInstance().changeUsername(user, newUsername)) {
            LOGGER.info("Username already used in connectedUserList, has not been updated.");
        } else {
            LOGGER.info(String.format("Username has been changed in connectedUserList: %s\n", newUsername));
            try {
                historyDAO.updateHistoryDB(user, newUsername);
            } catch (SQLException e) {
                LOGGER.severe("Could not update history on Received changeUsernameMessage for: " + newUsername);
                //throw new RuntimeException(e);
            }
        }
        displayConnectedUsers();
    }

    @Override
    public void onUserDisconnected(ConnectedUser user) {
        if (ConnectedUserList.getInstance().hasUsername(user.getUsername())) {
            ConnectedUserList.getInstance().removeConnectedUser(user);
            LOGGER.info("Removed user from connectedUserList: " + user);
        } else {
            LOGGER.info("User not found in connectedUserList: " + user);
        }
        displayConnectedUsers();
    }
}
