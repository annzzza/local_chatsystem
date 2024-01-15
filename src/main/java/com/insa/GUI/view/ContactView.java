package com.insa.GUI.view;

import com.google.gson.Gson;
import com.insa.network.connectedusers.ConnectedUser;
import com.insa.network.connectedusers.ConnectedUserList;
import com.insa.utils.MyLogger;

import javax.swing.*;

public class ContactView extends DefaultListModel<String> implements ConnectedUserList.Observer {
    private static final ContactView contactView = new ContactView();
    public static void initialize() {
        MyLogger.getInstance().info("Initializing ContactView");
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
        MyLogger.getInstance().info("Displaying contactView:");
        for (ConnectedUser connectedUser : ConnectedUserList.getInstance()) {
            MyLogger.getInstance().info("\t" + new Gson().toJson(connectedUser));
        }
    }

    @Override
    public void newConnectedUser(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Adding new connected user to contactView");
        this.addElement(connectedUser.getUsername());
        displayContactView();
    }

    @Override
    public void removeConnectedUser(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Removing connected user from contactView");
        this.removeElement(connectedUser.getUsername());
        displayContactView();
    }

    @Override
    public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
        MyLogger.getInstance().info("Changing username in contactView");
        this.removeElement(previousUsername);
        this.addElement(newConnectedUser.getUsername());
        displayContactView();
    }
}
