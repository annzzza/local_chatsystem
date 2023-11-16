package com.example.contactdiscovery;

import java.util.List;
import java.util.ArrayList;

public class ContactList  {
    List<ConnectedUser> contactList;

    public ContactList() {
        contactList = new ArrayList<ConnectedUser>();
    }
    public List<ConnectedUser> get() {
        return contactList;
    }

    public void add(ConnectedUser connectedUser) {
        this.contactList.add(connectedUser);
    }


    public boolean contains(String username) {
        return this.contactList.stream().anyMatch(connectedUser -> connectedUser.username.equals(username));
    }

}
