package com.insa.gui.view;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactViewTest {

    @BeforeEach
    void setUp() {
        ContactView.getInstance().clear();
        ConnectedUserList.getInstance().clear();
    }

    @Test
    void initialize() throws ConnectedUserAlreadyExists {
        ConnectedUserList.getInstance().addConnectedUser(new ConnectedUser("user1", null));

        ContactView.initialize();
        assertEquals(1, ContactView.getInstance().size());
    }

    @Test
    void getInstance() {
        ContactView contactView1 = ContactView.getInstance();
        ContactView contactView2 = ContactView.getInstance();
        assertEquals(contactView1, contactView2);
    }

    @Test
    void newConnectedUser() {
        ContactView.getInstance().newConnectedUser(new ConnectedUser("user1", null));
        assertEquals(1, ContactView.getInstance().size());
    }

    @Test
    void removeConnectedUser() {
        ContactView.getInstance().addElement("user1");
        ContactView.getInstance().removeConnectedUser(new ConnectedUser("user1", null));
        assertEquals(0, ContactView.getInstance().size());
    }

    @Test
    void usernameChanged() {
        ContactView.getInstance().addElement("user1");
        ContactView.getInstance().usernameChanged(new ConnectedUser("user2", null), "user1");
        assertEquals(1, ContactView.getInstance().size());
        assertEquals("user2", ContactView.getInstance().get(0));
    }
}