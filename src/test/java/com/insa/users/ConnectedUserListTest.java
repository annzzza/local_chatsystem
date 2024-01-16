package com.insa.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectedUserListTest {

    interface FallibleCode {
        void run() throws Exception;
    }

    private static void assertThrows(FallibleCode code) {
        try {
            code.run();
            throw new AssertionError("Expected exception was not thrown");
        } catch (Exception e) {
            // OK
        }
    }

    @BeforeEach
    public void clearConnectedUserList(){
        ConnectedUserList.getInstance().clear();
    }

    @Test
    void addConnectedUserTest() throws UnknownHostException, ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        ConnectedUser ronan = new ConnectedUser("ronan", InetAddress.getLocalHost());

        assertFalse(connectedUserList.contains(ronan));
        connectedUserList.addConnectedUser(ronan);
        assertTrue(connectedUserList.contains(ronan));

        assertFalse(connectedUserList.hasUsername("anna"));
        connectedUserList.addConnectedUser(new ConnectedUser("anna", InetAddress.getLocalHost()));
        assertTrue(connectedUserList.hasUsername("anna"));
        assertTrue(connectedUserList.contains(ronan));
    }

    @Test
    void duplicationTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        ConnectedUser testUser = new ConnectedUser("test", InetAddress.getLoopbackAddress());

        connectedUserList.addConnectedUser(testUser);
        assertTrue(connectedUserList.contains(testUser));

        assertThrows(() -> connectedUserList.addConnectedUser(testUser));
        assertThrows(() -> connectedUserList.addConnectedUser(testUser));
    }

    @Test
    void removeConnectedUserTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        ConnectedUser testUser = new ConnectedUser("test", InetAddress.getLoopbackAddress());
        assertFalse(connectedUserList.contains(testUser));
        connectedUserList.addConnectedUser(testUser);
        assertTrue(connectedUserList.contains(testUser));
        connectedUserList.removeConnectedUser(testUser);
        assertFalse(connectedUserList.contains(testUser));
    }
}