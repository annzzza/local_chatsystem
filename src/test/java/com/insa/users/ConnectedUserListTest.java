package com.insa.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ConnectedUserList class
 */
class ConnectedUserListTest {

    /**
     * Functional interface to test if a method throws an exception
     */
    interface FallibleCode {
        void run() throws Exception;
    }

    /**
     * Assert that the code throws an exception
     * @param code the code to test
     */
    private static void assertThrows(FallibleCode code) {
        try {
            code.run();
            throw new AssertionError("Expected exception was not thrown");
        } catch (Exception e) {
            // OK
        }
    }

    /**
     * Clear the connected user list before each test
     */
    @BeforeEach
    public void clearConnectedUserList(){
        ConnectedUserList.getInstance().clear();
    }


    /**
     * Test method to add connected user to the connected users list
     */
    @Test
    void addConnectedUserTest() throws UnknownHostException, ConnectedUserAlreadyExists {
        // Create a connected user
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        ConnectedUser ronan = new ConnectedUser("ronan", InetAddress.getLocalHost());

        // Check that the connected user is not in the connected user list
        assertFalse(connectedUserList.contains(ronan));

        // Add the user to the connected user list and check that it is in the connected user list
        connectedUserList.addConnectedUser(ronan);
        assertTrue(connectedUserList.contains(ronan));

        // Check that we can add another connected user
        assertFalse(connectedUserList.hasUsername("anna"));
        connectedUserList.addConnectedUser(new ConnectedUser("anna", InetAddress.getLocalHost()));
        assertTrue(connectedUserList.hasUsername("anna"));
        assertTrue(connectedUserList.contains(ronan));
    }

    /**
     * Test that will check that the connected user list cannot contain two connected users with the same username
     * @throws ConnectedUserAlreadyExists if the connected user already exists
     */
    @Test
    void duplicationTest() throws ConnectedUserAlreadyExists {
        // Create a connected user
        ConnectedUser testUser = new ConnectedUser("test", InetAddress.getLoopbackAddress());
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        assertEquals(0, connectedUserList.getAllConnectedUsers().size());

        // Add the connected user to the connected user list
        connectedUserList.addConnectedUser(testUser);
        assertTrue(connectedUserList.contains(testUser));
        assertEquals(1, connectedUserList.getAllConnectedUsers().size());

        // Check that we cannot add the same connected user twice
        assertThrows(() -> connectedUserList.addConnectedUser(testUser));
        assertEquals(1, connectedUserList.getAllConnectedUsers().size());
    }

    /**
     * Test that will check that the method to remove a connected user works
     */
    @Test
    void removeConnectedUserTest() throws ConnectedUserAlreadyExists {
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        assertEquals(0, connectedUserList.getAllConnectedUsers().size());

        // Create a connected user
        ConnectedUser testUser = new ConnectedUser("test", InetAddress.getLoopbackAddress());
        assertFalse(connectedUserList.contains(testUser));

        // Add the connected user to the connected user list
        connectedUserList.addConnectedUser(testUser);
        assertTrue(connectedUserList.contains(testUser));

        // Remove the connected user from the connected user list
        connectedUserList.removeConnectedUser(testUser);
        assertFalse(connectedUserList.contains(testUser));
        assertEquals(0, connectedUserList.getAllConnectedUsers().size());
    }

    /**
     * Test that will check that the method get a connected user works
     */
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


    /**
     * Test that will check that the method to get all connected users works
     * @throws ConnectedUserAlreadyExists if the connected user already exists
     */
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

        // Add observer to the connected user list that will check different events
        connectedUserList.addObserver(new ConnectedUserList.Observer() {
            @Override
            public void newConnectedUser(ConnectedUser connectedUser) {
                // Check that the connected user is in the connected user list
                assertTrue(connectedUserList.contains(connectedUser));
            }

            @Override
            public void removeConnectedUser(ConnectedUser connectedUser) {
                // Check that the connected user is not in the connected user list
                assertFalse(connectedUserList.contains(connectedUser));
            }

            @Override
            public void usernameChanged(ConnectedUser newConnectedUser, String previousUsername) {
                // Check that the username has changed
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
        connectedUserList.clear();
    }
}