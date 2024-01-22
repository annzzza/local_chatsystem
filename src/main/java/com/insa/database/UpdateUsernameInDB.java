package com.insa.database;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import com.insa.utils.MyLogger;


/**
 * Update the username in the database when a user changes his username
 */
public class UpdateUsernameInDB implements ConnectedUserList.Observer {
    private static final MyLogger LOGGER = new MyLogger(UpdateUsernameInDB.class.getName());

    /**
     * HistoryDAO instance
     */
    HistoryDAO historyDAO = new HistoryDAO();

    /**
     * The database is not updated when a new user connects
     * @param connectedUser The user that connected
     */
    @Override
    public void newConnectedUser(ConnectedUser connectedUser) {}

    /**
     * The database is not updated when a user disconnects
     * @param connectedUser The user that disconnected
     */
    @Override
    public void removeConnectedUser(ConnectedUser connectedUser) {}


    /**
     * Update the username in the database when a user changes his username
     * It is used to keep the history of the messages
     * @param newConnectedUser The user that changed their username
     * @param previousUsername The previous username of the user
     */
    @Override
    public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
        try {
            historyDAO.updateHistoryDB(new User(previousUsername), newConnectedUser.getUsername());
        } catch (DAOException e) {
            LOGGER.severe(e.getMessage());
        }
    }
}
