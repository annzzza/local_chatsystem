package com.example.contactdiscovery;

import java.util.List;
import java.util.ArrayList;

public class ContactList  {
    List<Contact> contactList;

    public ContactList() {
        contactList = new ArrayList<Contact>();
    }
    public List<Contact> get() {
        return contactList;
    }

    public void add(Contact contact) {
        this.contactList.add(contact);
    }


    public boolean contains(String username) {
        return this.contactList.stream().anyMatch(contact -> contact.username.equals(username));
    }

}
