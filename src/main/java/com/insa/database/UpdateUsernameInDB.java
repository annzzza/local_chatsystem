package com.insa.database;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import com.insa.utils.MyLogger;

import java.sql.SQLException;

public class UpdateUsernameInDB implements ConnectedUserList.Observer {
    private static final MyLogger LOGGER = new MyLogger(UpdateUsernameInDB.class.getName());
    HistoryDAO historyDAO = new HistoryDAO();

    @Override
    public void newConnectedUser(ConnectedUser connectedUser) {

    }

    @Override
    public void removeConnectedUser(ConnectedUser connectedUser) {

    }

    @Override
    public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
        try {
            historyDAO.updateHistoryDB(new User(previousUsername), newConnectedUser.getUsername());
        } catch (SQLException e) {
            LOGGER.severe("Could not update history on Received changeUsernameMessage for: " + newConnectedUser.getUsername());
        }
    }
}
