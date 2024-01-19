package com.insa.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void getConnectedUserTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();

        // Create a connected user
        ConnectedUser testUser = new ConnectedUser("test", InetAddress.getLoopbackAddress());

        // Add it to the connected user list
        connectedUserList.addConnectedUser(testUser);

        // Get the connected user
        ConnectedUser connectedUser = connectedUserList.getConnectedUser(testUser.getUsername());

        // Check that the connected user is the same
        assertEquals(testUser, connectedUser);
    }

    @Test
    void getAllConnectedUserTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();

        // Create a list of connected users
        List<ConnectedUser> connectedUsers = Arrays.asList(
                new ConnectedUser("test1", InetAddress.getLoopbackAddress()),
                new ConnectedUser("test2", InetAddress.getLoopbackAddress()),
                new ConnectedUser("test3", InetAddress.getLoopbackAddress())
        );

        // Add them to the connected user list
        for(ConnectedUser cu: connectedUsers){
            connectedUserList.addConnectedUser(cu);
        }

        // Get all connected users
        List<ConnectedUser> allConnectedUsers = connectedUserList.getAllConnectedUsers();

        // Check that the list of connected users is the same
        assertEquals(connectedUsers.size(), allConnectedUsers.size());
        assertEquals(connectedUsers, allConnectedUsers);
    }


    /**
     * Test that will check that all the observers are notified
     */
    @Test
    void observerTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();

        // Create a list of connected users
        List<ConnectedUser> connectedUsers = Arrays.asList(
                new ConnectedUser("test1", InetAddress.getLoopbackAddress()),
                new ConnectedUser("test2", InetAddress.getLoopbackAddress()),
                new ConnectedUser("test3", InetAddress.getLoopbackAddress())
        );

        // Add observer to the connected user list
        connectedUserList.addObserver(new ConnectedUserList.Observer() {
            @Override
            public void newConnectedUser(ConnectedUser connectedUser) {
                assertTrue(connectedUserList.contains(connectedUser));
            }

            @Override
            public void removeConnectedUser(ConnectedUser connectedUser) {
                assertFalse(connectedUserList.contains(connectedUser));
            }

            @Override
            public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
                assertTrue(connectedUserList.hasUsername(newConnectedUser.getUsername()));
                assertFalse(connectedUserList.hasUsername(previousUsername));
            }
        });


        // Add connected users to the connected user list
        for(ConnectedUser cu: connectedUsers){
            connectedUserList.addConnectedUser(cu);
        }
        assertEquals(connectedUsers.size(), connectedUserList.getAllConnectedUsers().size());

        // Change the username of the first connected user
        connectedUserList.changeUsername(connectedUserList.getConnectedUser(connectedUsers.get(0).getUsername()), "test4");
        assertEquals(connectedUsers.size(), connectedUserList.getAllConnectedUsers().size());


        // Remove all connected users
        for(ConnectedUser cu: connectedUsers){
            connectedUserList.removeConnectedUser(cu);
        }
    }
}