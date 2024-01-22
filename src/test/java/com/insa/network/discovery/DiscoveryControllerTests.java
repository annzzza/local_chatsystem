package com.insa.network.discovery;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

public class DiscoveryControllerTests {

    @BeforeEach
    void init() {
        // Init connected user list
        ConnectedUserList.getInstance().clear();
        assertTrue(ConnectedUserList.getInstance().isEmpty());
    }

    /**
     * Test current user methods
     */
    @Test
    void testCurrentUser() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Check that the current user is set
        assertEquals(username, controller.getCurrentUser().getUsername());

        // Check that the current user can be changed when in the same package
        String username2 = "test2";
        controller.setCurrentUser(new User(username2));
        assertEquals(username2, controller.getCurrentUser().getUsername());
    }

    /**
     * Test of discovery messages
     */
    @Test
    void testDiscovery() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Check that a second user can be added
        message.setSender(new User("test3"));
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(message.getSender().getUsername()));

        // Check that a user cannot be added twice
        controller.handleMessage(message);
        assertEquals(2, ConnectedUserList.getInstance().size());
    }

    /**
     * Test of discovery messages with username the same as self
     */
    @Test
    void testDiscoveryWithSelfUsername() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User(username);
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is not added to the list
        assertEquals(0, ConnectedUserList.getInstance().size());
        assertFalse(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
    }

    /**
     * Test of connection messages
     */
    @Test
    void testAnswerReceived() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send connection message
        message.setType(UDPMessage.MessageType.USER_CONNECTED);
        controller.handleMessage(message);

        // Check that the user is still in the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Check that a second user can be added
        message.setSender(new User("test3"));
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(message.getSender().getUsername()));

    }

    /**
     * Test of connection messages with username the same as self
     * E.g : when the user is trying to connect with another connected user's username
     */
    @Test
    void testAnswerReceivedWithSelfUsername() throws ConnectedUserAlreadyExists {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // We add a user to the list
        User user = new User("test");
        ConnectedUser connectedUser = new ConnectedUser(user, InetAddress.getLoopbackAddress());
        ConnectedUserList.getInstance().addConnectedUser(connectedUser);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(connectedUser.getUsername()));

        // Other user with same username sends answer after receiving discovery message
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.USER_CONNECTED);
        message.setSender(user);
        controller.handleMessage(message);

        // Check that the user is still in the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(user.getUsername()));
        assertNull(controller.getCurrentUser());
    }

    /**
     * Test of username change messages
     */
    @Test
    void testChangeUsername() {
        // Create controller
        User user = new User("test");
        DiscoveryController controller = new DiscoveryController(user);

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send username change message
        message.setType(UDPMessage.MessageType.USERNAME_CHANGED);
        message.setContent("test3");
        controller.handleMessage(message);

        // Check that the username is changed
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(message.getContent()));

        // Check that the old username is not in the list
        assertFalse(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
    }

    @Test
    void testChangeUsernameWithSelfUsername() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send username change message
        message.setType(UDPMessage.MessageType.USERNAME_CHANGED);
        message.setContent(username);
        controller.handleMessage(message);

        // Check that the username is not changed
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
    }

    /**
     * Test of username change messages with user already in the list
     */
    @Test
    void testChangeUsernameWithExistingUser() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send username change message
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender2 = new User("test3");
        message.setSender(sender2);
        controller.handleMessage(message);

        // Check that both users are in the list
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender2.getUsername()));

        // Send username change message
        message.setType(UDPMessage.MessageType.USERNAME_CHANGED);
        message.setContent(sender.getUsername());
        controller.handleMessage(message);

        // Check that the username is not changed
        assertEquals(2, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender2.getUsername()));
    }

    /**
     * Test of disconnection messages
     */
    @Test
    void testDisconnection() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send disconnection message
        message.setType(UDPMessage.MessageType.USER_DISCONNECTED);
        controller.handleMessage(message);

        // Check that the user is removed from the list
        assertEquals(0, ConnectedUserList.getInstance().size());
        assertFalse(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
    }

    /**
     * Test that disconnection does not remove other users if unknown user is given
     */
    @Test
    void testDisconnectionWithUnknownUser() {
        // Create controller
        String username = "test";
        DiscoveryController controller = new DiscoveryController(new User(username));

        // Send discovery message to add user
        UDPMessage message = new UDPMessage();
        message.setType(UDPMessage.MessageType.DISCOVERY);
        User sender = new User("test2");
        message.setSender(sender);
        message.setSenderIP(InetAddress.getLoopbackAddress());
        controller.handleMessage(message);

        // Check that the user is added to the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));

        // Send disconnection message
        message.setType(UDPMessage.MessageType.USER_DISCONNECTED);
        message.setSender(new User("test3"));
        controller.handleMessage(message);

        // Check that the user is still in the list
        assertEquals(1, ConnectedUserList.getInstance().size());
        assertTrue(ConnectedUserList.getInstance().hasUsername(sender.getUsername()));
    }

}
