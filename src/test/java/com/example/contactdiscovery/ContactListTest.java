package com.example.contactdiscovery;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class ContactListTest {

    @Test
    void isUsernameInList() {
        ContactList contactList = new ContactList();
        contactList.add(new ConnectedUser("Ronan", "qwerty", InetAddress.getLoopbackAddress()));
        contactList.add(new ConnectedUser("Anna", "azerty", InetAddress.getLoopbackAddress()));
        assertEquals(contactList.contains("Ronan"), true);
        assertEquals(contactList.contains("Bob"), false);
    }
}