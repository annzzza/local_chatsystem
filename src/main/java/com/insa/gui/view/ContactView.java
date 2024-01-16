package com.insa.gui.view;

import com.google.gson.Gson;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import javax.swing.*;

public class ContactView extends DefaultListModel<String> implements ConnectedUserList.Observer {
    private static final MyLogger LOGGER = new MyLogger(ContactView.class.getName());

    private static final ContactView contactView = new ContactView();
    public static void initialize() {
        LOGGER.info("Initializing ContactView");
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        for (ConnectedUser connectedUser : connectedUserList) {
            contactView.addElement(connectedUser.getUsername());
        }
        connectedUserList.addObserver(contactView);
    }

    public static ContactView getInstance() {
        return contactView;
    }

    private void displayContactView() {
        LOGGER.info("Displaying contactView:");
        for (ConnectedUser connectedUser : ConnectedUserList.getInstance()) {
            LOGGER.info("\t" + new Gson().toJson(connectedUser));
        }
    }

    @Override
    public void newConnectedUser(ConnectedUser connectedUser) {
        LOGGER.info("Adding new connected user to contactView");
        this.addElement(connectedUser.getUsername());
        displayContactView();
    }

    @Override
    public void removeConnectedUser(ConnectedUser connectedUser) {
        LOGGER.info("Removing connected user from contactView");
        this.removeElement(connectedUser.getUsername());
        displayContactView();
    }

    @Override
    public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
        LOGGER.info("Changing username in contactView");
        this.set(this.indexOf(previousUsername), newConnectedUser.getUsername());
//        this.removeElement(previousUsername);
//        this.addElement(newConnectedUser.getUsername());
        displayContactView();
    }
}
