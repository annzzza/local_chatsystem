package com.insa.gui.view;

import com.google.gson.Gson;
import com.insa.database.UpdateUsernameInDB;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import javax.swing.*;


/**
 * Singleton class that represents the list of connected users visible in the GUI
 * It is a DefaultListModel<String> and an Observer of ConnectedUserList
 */
public class ContactView extends DefaultListModel<String> implements ConnectedUserList.Observer {
    private static final MyLogger LOGGER = new MyLogger(ContactView.class.getName());

    /**
     * Singleton instance
     */
    private static final ContactView contactView = new ContactView();

    /**
     * Method to initialize the contactView
     * It adds all the connected users to the contactView
     */
    public static void initialize() {
        LOGGER.info("Initializing ContactView");
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        for (ConnectedUser connectedUser : connectedUserList) {
            contactView.addElement(connectedUser.getUsername());
        }
        connectedUserList.addObserver(contactView);
        connectedUserList.addObserver(new UpdateUsernameInDB());
    }

    /**
     * Method to get singleton instance
     * @return singleton instance
     */
    public static ContactView getInstance() {
        return contactView;
    }

    /**
     * Display the contact list in log
     */
    private void displayContactView() {
        LOGGER.info("Displaying contactView:");
        for (ConnectedUser connectedUser : ConnectedUserList.getInstance()) {
            LOGGER.info("\t" + new Gson().toJson(connectedUser));
        }
    }

    /**
     * Update contact list view when a new user is connected
     * @param connectedUser The user that connected
     */
    @Override
    public void newConnectedUser(ConnectedUser connectedUser) {
        LOGGER.info("Adding new connected user to contactView");
        this.addElement(connectedUser.getUsername());
        displayContactView();
    }

    /**
     * Update contact list view when a user disconnected
     * @param connectedUser The user that disconnected
     */
    @Override
    public void removeConnectedUser(ConnectedUser connectedUser) {
        LOGGER.info("Removing connected user from contactView");
        this.removeElement(connectedUser.getUsername());
        displayContactView();
    }

    /**
     * Update contact lsit viw when a user changed username
     * @param newConnectedUser The user that changed their username
     * @param previousUsername The previous username of the user
     */
    @Override
    public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
        LOGGER.info("Changing username in contactView: " + previousUsername + " -> " + newConnectedUser.getUsername() );
        this.set(this.indexOf(previousUsername), newConnectedUser.getUsername());
        displayContactView();
    }
}
